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