// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.intellij.plugins.markdown.google.accounts.data

data class GoogleUserInfo(
  val id: String,
  val email: String,
  val verifiedEmail: Boolean,
  val name: String,
  val givenName: String,
  val familyName: String,
  val picture: String,
  val locale: String,
  val hd: String? = null
)
