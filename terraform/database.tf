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
  username = "postgres"
  password = "postgres"
  port = "5432"

  vpc_security_group_ids = [aws_security_group.database.id]

  maintenance_window = "Thu:03:30-Thu:05:30"
  backup_window = "05:30-06:30"

  # disable backups to create DB faster
  backup_retention_period = 0

  # DB subnet group
  subnet_ids = "${flatten(chunklist(aws_subnet.private_subnet.*.id, 1))}"

  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]

  storage_type = "gp2"
  multi_az = "false"
  family = "postgres10"
  major_engine_version = "10.4"
  create_db_option_group = false

  # Database Deletion Protection
  deletion_protection = false

}