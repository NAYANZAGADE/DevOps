resource "aws_eks_addon" "metrics_server" {
  cluster_name = aws_eks_cluster.eks.name
  addon_name   = "metrics-server"
  depends_on   = [aws_eks_node_group.node_group]
}

resource "aws_eks_addon" "pod_identity" {
  cluster_name = aws_eks_cluster.eks.name
  addon_name   = "eks-pod-identity-agent"
  depends_on   = [aws_eks_node_group.node_group]
}

# resource "aws_eks_addon" "EBS_CSI_Driver" {
#   cluster_name = aws_eks_cluster.eks.name
#   addon_name   = "aws-ebs-csi-driver"
#   depends_on   = [aws_eks_node_group.node_group]
# }
