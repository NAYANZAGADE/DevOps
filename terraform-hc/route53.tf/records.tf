data "aws_route53_zone" "selected" {
  name         = "nayanzagade.xyz"
}


resource "aws_route53_record" "www" {
  zone_id = data.aws_route53_zone.selected.zone_id
  name    = "httpd.nayanzagade.xyz"
  type    = "A"
  alias {
    name                   = var.alb_dns_name
    zone_id                = var.alb_zone_id
    evaluate_target_health = true
  }
}