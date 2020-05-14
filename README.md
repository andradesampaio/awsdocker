# awsdocker
Spring Boot, Aws e Terraform

# Criando Postgres no docker

docker run -p 5432:5432 --name beerdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=beerstore -d postgres:10.5-alpine 

# Configurando Terraform
depois de instalar criar link simbolico
ln -s /onde_esta_executavel /usr/local/bin/terraform

criar um usuario na aws console para o terraform em IAM - baixar e salvar key ID e o secretacess key


# Instalar Aws Cli
configurar o profile do terraform
aws configure --profile terraform(ou nome do profile)
lembrar: Access Key ID 
        Secret Access Key
Default region name = us-east-1
Default output format = text 

criar arquivo main.tf

provider "aws" {
  version = "~> 2.7"
  profile = "terraform"
  shared_credentials_file = "~/.aws/credentials"
}

executar o comando: terraform init para inicializar

# Armazenando seu arquivo de configuracao do terraform no S3
Criar um bucket na aws console
criar arquivo terraform.tf

terraform {
  backend "s3"{
    bucket = "asampaio-terraform-starte"
    key = "beerstore-online"
    region = "us-east-1"
    profile = "terraform"
  }
}

executar o comando: terraform init para inicializar

# Provisionando a VPC para sua rede na Aws
criar arquivo network.tf

resource "aws_vpc" "main" {
  cidr_block = "192.168.0.0/16"
  tags = {
    Name = "beerNetwork"
  }
}

executar o comando: terraform apply para aplicar e criar sua rede

Default region name = us-east-1
Enter a value: (yes / no)

# Provisionando as subnets privadas

Criando tres subnet 192.168.10.0 - 192.168.20.0 - 192.168.30.0/24
Adcionar as configuracoes a baixo no arquivo network.tf

resource "aws_subnet" "private_subnet" {
  count = 3

  vpc_id = "${aws_vpc.main.id}"

  //192.168.10.0/24
  cidr_block = "${cidrsubnet(aws_vpc.main.cidr_block, 8, (count.index + 1) * 10)}"
  //"us-east-1a"
  availability_zone = "${var.availability_zones[count.index]}"
  tags {
    Name = "kmvpsolutions_private_subnet_${count.index+1}"
  }
}

criar arquivo variables.tf
variable "availability_zone" {
  default = [
    "us-east-1a",
    "us-east-1b",
    "us-east-1c"
  ]
}

executar o comando: terraform apply para aplicar e criar sua rede e sub-redes

Default region name = us-east-1
Enter a value: (yes / no)

# Criando chave assimétrica para acessar as instâncias
criar uma pasta key

comando para gerar as Keys: ssh-keygen -t rsa -b 4096 -o -a 100 -f key/beerstore_key

associar key publica nas instancias 
resource "aws_key_pair" "keypair" {
  public_key = "${file("key/beerstore_key.pub")}"
}

# Criando Security Group para liberar acesso
criar um arquivo security
pegar o ip fixo linha de comando 
curl -s ipinfo.io/ip

resource "aws_security_group" "allow_ssh" {
  vpc_id = "${aws_vpc.main.id}"
  name = "security_allow_ssh"
  ingress {
    from_port = 22
    to_port =22
    protocol = "tcp"
    cidr_blocks = ["189.77.182.237/32"]
  }
}

adcionar no arquivo de instancia o security_group que voce criou 

vpc_security_group_ids = ["${aws_security_group.allow_ssh.id}"]

# Criando Internet Gateway para acessar instâncias pela internet
no arquivo network vamos configurar route, gateway e associar 

criando a gateway
resource "aws_internet_gateway" "gtw" {
  vpc_id = "${aws_vpc.main.id}"
}

criando rota para gateway
resource "aws_route_table" "route_gtw" {
   vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.gateway.id}"
  }
}


Fazendo a associação

resource "aws_route_table_association" "route_table_associatio" {
  count = 3
  route_table_id = "${aws_route_table.route_gateway.id}"
  subnet_id = "${element(aws_subnet.public_subnet.*.id, count.index)}"
}


comando para alterar permissoes no arquivo da Key
chmod 400 beerstore_key
Testando a conexão 
ssh -i key/beerstore_key ec2-user@11.141.125.125


#Provisionando Postgres com RDS
criar arquivo database.tf
module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 2.0"
  identifier = "beerstore-rds"
  engine = "postgres"
  engine_version = "10.4"
  instance_class = "db.t2.micro"
  allocated_storage = 5
  storage_encrypted = false
  name = "beerstore"
  username = "password"
  password = "password"
  port = "5432"
  vpc_security_group_ids = [aws_security_group.database.id]
  maintenance_window = "Thu:03:30-Thu:05:30"
  backup_window = "05:30-06:30"
  backup_retention_period = 0
  subnet_ids = "${flatten(chunklist(aws_subnet.private_subnet.*.id, 1))}"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]
  storage_type = "gp2"
  multi_az = "false"
  family = "postgres10"
  major_engine_version = "10.4"
  create_db_option_group = false
  deletion_protection = false 
}

#Destruindo o ambiente todo
comando terraform destroy




