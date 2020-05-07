provider "aws" {
  version = "~> 2.7"
  profile = "terraform"
  shared_credentials_file = "~/.aws/credentials"
}