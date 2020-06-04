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

comando para alterar permissoes no arquivo da Key
chmod 400 beerstore_key

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


##Fazendo a associação

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

#Criando sua imagem e dockerizando a aplicação Java
criar um arquivo Dockefile
FROM openjdk:15-jdk-alpine
MAINTAINER Andrade Sampaio <asampaio3006@gmail.com>
RUN apk add --update bash
ENV LANG C.UTF-8
ADD build/libs/*.jar /app/app.jar
CMD java -jar /app/app.jar $APP_OPTIONS
###########################################
"APP_OPTIONS" para passar paramentros na inicializaçao do docker
Ex: APP_OPTIONS='--spring.datasource.url=jdbc:postgresql://beerdb:5432/beerstore'
###########################################
#Fazer build da imagem 

docker build -t ID_DOCKERHUB/NOME_APLICAÇAO:0.1 .

## Iniciar container
docker run -p 8080:8080 asampaio3006/beerstore:0.1

#Criando uma rede docker para comunicação dos containers
criar rede 
docker network create Nome_da_Rede

adcionar container na rede
docker network connect Nome_da_Rede
inciar um container e add ele na rede.

Opção
-p -> porta da aplicação
--network -> rede que o container vai usar 
-e -> passar paramentros
--rm --> para remover o container depois de finalizar

docker run --rm -p 8080:8080 --network beer-network -e APP_OPTIONS='--spring.datasource.url=jdbc:postgresql://beerdb:5432/beerstore' asampaio3006/beerstore:0.1

###Criando o primeiro playbook Ansible
criar arquivo playbook.yml
---
- name: Ensure Docker is installed
  hosts: all
  remote_user: ec2-user
  gather_facts: false
  become: true
  tasks:
    - name: Install Docker
      yum: name=docker

    - name: Ensure docker service is started and enabled
      service:
        name: docker
        state: started
        enabled: yes

    - name: Create directory for Portainer
      file:
        path: .portainer/data
        state: directory
        owner: ec2-user
        group: ec2-user

##Automatizando execução do Terraform com IP público
crie um arquivo hosts.tpl
onde serão salvo os ips criado na construção do ec2

${PUBLIC_IP_0}
${PUBLIC_IP_1}
${PUBLIC_IP_2}


no arquivo instance.tl adcione 
vamos atribuir os ips no arquivo hosts

data "template_file" "hosts" {
  template = "${file("./template/hosts.tpl")}"

  vars = {
    PUBLIC_IP_0 = "${aws_instance.instances.*.public_ip[0]}"
    PUBLIC_IP_1 = "${aws_instance.instances.*.public_ip[1]}"
    PUBLIC_IP_2 = "${aws_instance.instances.*.public_ip[2]}"
  }
}

resource "local_file" "hosts" {
  content = "${data.template_file.hosts.rendered}"
  filename = "./hosts"
}

pegando ip externo 

no arquivo variables adcione 
variable "my_public_ip" {}

no arquivo security.tl adcione 
  cidr_blocks = ["${var.my_public_ip}"]

vamos criar um arquivo run-terraform.tl
#!/bin/bash

echo "Provisioning infrastructure..."

echo "Finding my public ip address..."
MY_PUBLIC_IP="$(curl -s ipinfo.io/ip)"
echo "... your public ip is $MY_PUBLIC_IP"

echo "Starting terraform..."
terraform apply -var "my_public_ip=$MY_PUBLIC_IP/32"

##Autorizando outbound via Security Group
no arquivo security.tl
adicione essa regra

resource "aws_security_group" "allow_outbound" {
  vpc_id = "${aws_vpc.main.id}"
  name = "hibicode_allow_outbound"

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

}

##Instalando o Docker nas máquinas EC2 com Ansible

vamos criar um arquivo run-ansible.sh

#!/bin/bash

echo "Starting ansible..."
#comando para nao solicitar confirmação de conexao
ANSIBLE_HOST_KEY_CHECKING=false
ansible-playbook -i ../terraform/hosts --private-key ../terraform/key/beerstore_key beerstore-playbook.yml -v

##Criando Security Group para Swarm
no arquivo security.tl
adicione essa regra

resource "aws_security_group" "cluster_communication" {
  vpc_id = "${aws_vpc.main.id}"
  name = "hibicode_cluster_communication"

  ingress {
    from_port = 2377
    to_port = 2377
    protocol = "tcp"
    self = true
  }

  ingress {
    from_port = 7946
    to_port = 7946
    protocol = "tcp"
    self = true
  }

  ingress {
    from_port = 7946
    to_port = 7946
    protocol = "udp"
    self = true
  }

  ingress {
    from_port = 4789
    to_port = 4789
    protocol = "udp"
    self = true
  }

}

no arquivo instances.tl
adcione a nova regra criada 
vpc_security_group_ids =["${aws_security_group.cluster_communication.id}"]

##Deploy da aplicação no Cluster
acesso
ssh -i ./key/beerstore_key ec2-user@IP

sudo docker service create --name <NAME_CONTAINER> -e 
SPRING_DATASOURCE_URL=jdbc:postgresql://URL_DO_RDS_DA_SUA_CONTA:5432/<NAME_DATABASE> -p 8080:8080 --network service <NOME_IMAGEM>:<VERSAO>













