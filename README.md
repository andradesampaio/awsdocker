# awsdocker
Spring Boot,Kotlin, Aws e Terraform

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



