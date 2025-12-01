module "Networking" {
    source = "./Networking"

    cidr_block  = var.cidr_block
    cluster_name = var.cluster_name
    region = var.region
    env = var.env
}

module "iam" {
    source = "./iam"
    cluster_name = var.cluster_name
    env = var.env
    region = var.region
    eks_version = var.eks_version
  
}

module "EKS" {
    source = "./EKS"

    cluster_name = var.cluster_name
    env          = var.env
    region       = var.region
    eks_version  = var.eks_version
    
    # Pass networking outputs
    private_subnet_ids = module.Networking.private_subnet_ids
    
    # Pass IAM outputs
    eks_role_arn = module.iam.eks_role_arn
    ng_role_arn = module.iam.ng_role_arn
    cluster_policy_attachment = module.iam.cluster_policy_attachment
    
    depends_on = [module.Networking, module.iam]
}

# module "security_groups" {
#   source = "./security-groups"
#   vpc_id   = module.Networking.vpc_id
#   vpc_cidr = var.cidr_block
#   env      = var.env
  
#   depends_on = [module.Networking]
# }

# module "ALB" {
#   source = "./ALB"
#   region             = var.region
#   public_subnet_ids  = module.Networking.public_subnet_ids
#   alb_sg_id          = module.security_groups.alb_sg_id
  
#   depends_on = [module.security_groups]
# }