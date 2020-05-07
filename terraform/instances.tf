resource "aws_instance" "instances" {
  count = 3
  ami = "ami-0323c3dd2da7fb37d"
  instance_type = "t2.micro"

  subnet_id = "${element(aws_subnet.public_subnet.*.id, count.index)}"

  tags = {
    Name = "network_instances"
  }

}