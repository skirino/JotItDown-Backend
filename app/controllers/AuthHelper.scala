package controllers

import java.security.MessageDigest

object AuthHelper {
  val salt = "JotItDown's salt"
  val md   = MessageDigest.getInstance("SHA-1")

  def genCredential(id: String, pw: String) = {
    if(!validUserInfo(id, pw))
      throw new RuntimeException("Invalid user info!")
    digest(salt + id + pw)
  }

  private def digest(str: String) = {
    md.update(str.getBytes)
    md.digest.foldLeft("") { (s, b) => s + "%02x".format(if(b < 0) b + 256 else b) }
  }

  private def validUserInfo(id: String, pw: String) = {
    !id.contains('/')
  }
}
