resource "aws_eks_cluster" "eks" {
  name = "${var.env}-${var.cluster_name}"

  access_config {
    authentication_mode = "API"
    bootstrap_cluster_creator_admin_permissions = true
  }

  role_arn = var.eks_role_arn
  version  = var.eks_version

  vpc_config {
    endpoint_private_access = false
    endpoint_public_access = true

    subnet_ids = var.private_subnet_ids
  }

  # Ensure that IAM Role permissions are created before and deleted
  # after EKS Cluster handling. Otherwise, EKS will not be able to
  # properly delete EKS managed EC2 infrastructure such as Security Groups.
  depends_on = [
    var.cluster_policy_attachment,
  ]
}