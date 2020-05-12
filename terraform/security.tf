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

resource "aws_security_group" "database" {
  vpc_id = "${aws_vpc.main.id}"
  name = "beerstore_database"

  ingress {
    from_port = 5432
    protocol = "tcp"
    to_port = 5432
    self = true
  }
}