resource "aws_vpc" "main" {
  cidr_block = "192.168.0.0/16"
  tags = {
    Name = "beerNetwork"
  }
}

resource "aws_subnet" "private_subnet" {
  count = 3

  vpc_id = "${aws_vpc.main.id}"
  cidr_block = "${cidrsubnet(aws_vpc.main.cidr_block, 8, (count.index + 1) * 10)}"
  availability_zone = "${var.availability_zone[count.index]}"

  tags = {
    Name = "beerNetwork_private_subnet_${count.index}"
  }
}

resource "aws_subnet" "public_subnet" {
  count = 3

  vpc_id = "${aws_vpc.main.id}"
  cidr_block = "${cidrsubnet(aws_vpc.main.cidr_block, 8, (count.index + 1) * 11)}"
  availability_zone = "${var.availability_zone[count.index]}"
  map_public_ip_on_launch = true

  tags = {
    Name = "beerNetwork_private_subnet_${count.index}"
  }
}