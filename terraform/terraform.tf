terraform {
  backend "s3"{
    bucket = "asampaio-terraform-starte"
    key = "beerstore-online"
    region = "us-east-1"
    profile = "terraform"
  }
}