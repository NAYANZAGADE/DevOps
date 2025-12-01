module "networking" {
  source = "./networking"
  
  cidr_block = var.cidr_block
  env        = var.env
  region     = var.region
}

module "security_group" {
  source = "./security-group"
  
  env      = var.env
  vpc_id   = module.networking.vpc_id
  vpc_cidr = module.networking.vpc_cidr
}

module "iam" {
  source = "./iam"
  
  env = var.env
}

module "ecs" {
  source = "./ecs"
  
  env                        = var.env
  region                     = var.region
  instance_type              = var.instance_type
  keyname                    = var.keyname
  vpc_id                     = module.networking.vpc_id
  vpc_cidr                   = module.networking.vpc_cidr
  private_subnet_ids         = module.networking.private_subnet_ids
  public_subnet_ids          = module.networking.public_subnet_ids
  ecs_instance_profile_name  = module.iam.ecs_instance_profile_name
  ecs_sg_id                  = module.security_group.ecs_sg_id
}

module "alb" {
  source = "./alb.tf"
  
  env                = var.env
  vpc_id             = module.networking.vpc_id
  public_subnet_ids  = module.networking.public_subnet_ids
  alb_sg_id          = module.security_group.alb_sg_id
  asg_name           = module.ecs.asg_name
  certificate_arn    = module.route53.certificate_arn
}

module "route53" {
  source = "./route53.tf"
  
  env           = var.env
  alb_dns_name  = module.alb.alb_dns_name
  alb_zone_id   = module.alb.alb_zone_id
}