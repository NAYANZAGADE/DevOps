# resource "aws_iam_user" "developer" {
#   name = "Developer"
# }


# resource "aws_iam_policy" "developer_eks_policy" {
#   name        = "test_policy"
#   description = "Developer policy for eks"

#   # Terraform's "jsonencode" function converts a
#   # Terraform expression result to valid JSON syntax.
#   policy = jsonencode({
#     Version = "2012-10-17"
#     Statement = [
#       {
#         Action = [
#           "eks:DescribeCLuster",
#           "eks:ListClusters"
#         ]
#         Effect   = "Allow"
#         Resource = "*"
#       }
#     ]
#   })
# }

# resource "aws_iam_user_policy_attachment" "developer_attach" {
#   user       = aws_iam_user.developer.name
#   policy_arn = aws_iam_policy.developer_eks_policy.arn
# }


# resource "aws_eks_access_entry" "Developer_entry" {
#   cluster_name      = aws_eks_cluster.eks.name
#   principal_arn     = aws_iam_user.developer.arn
#   kubernetes_groups = ["my-viewer"]
# }

# resource "aws_eks_access_policy_association" "developer_access" {
#   cluster_name  = aws_eks_cluster.eks.name
#   principal_arn = aws_iam_user.developer.arn
#   policy_arn    = "arn:aws:eks::aws:cluster-access-policy/AmazonEKSViewPolicy"

#   access_scope {
#     type = "cluster"
#   }
# }