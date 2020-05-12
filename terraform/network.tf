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

resource "aws_internet_gateway" "gateway" {
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_route_table" "route_gateway" {
   vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.gateway.id}"
  }
}

resource "aws_route_table_association" "route_table_associatio" {
  count = 3
  route_table_id = "${aws_route_table.route_gateway.id}"
  subnet_id = "${element(aws_subnet.public_subnet.*.id, count.index)}"
}