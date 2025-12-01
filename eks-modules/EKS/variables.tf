variable "region" {
    description = "AWS REGION"
    type = string
}

variable "env" {
    description = "ENVIRONMENT"
    type = string
}


variable "eks_version" {
    description = "EKS VERSION"
    type = string
}  

variable "cluster_name" {
    description = "EKS CLUSTER NAME "
    type = string
}

variable "private_subnet_ids" {
    description = "Private subnet IDs for EKS cluster"
    type = list(string)
}

variable "eks_role_arn" {
    description = "EKS cluster IAM role ARN"
    type = string
}

variable "cluster_policy_attachment" {
    description = "EKS cluster policy attachment"
}

variable "ng_role_arn" {
    description = "EKS node group IAM role ARN"
    type = string
}

