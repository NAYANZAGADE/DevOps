output "eks_role_arn" {
  value = aws_iam_role.eks_role.arn
}

output "ng_role_arn" {
  value = aws_iam_role.ng_role.arn
}

output "cluster_policy_attachment" {
  value = aws_iam_role_policy_attachment.cluster_AmazonEKSClusterPolicy
}

