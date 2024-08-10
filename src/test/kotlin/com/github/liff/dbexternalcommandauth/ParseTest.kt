package com.github.liff.dbexternalcommandauth

import com.intellij.credentialStore.Credentials
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.should

class ParseTest :
    StringSpec({
        "pass-like (1)" {
            val creds =
                parse(
                    """
                    PaSS
                    User
                    """.trimIndent(),
                )

            creds should beEqual(Credentials("User", "PaSS"))
        }

        "pass-like (2)" {
            val creds =
                parse(
                    """
                    PaSS
                    Username: User
                    """.trimIndent(),
                )

            creds should beEqual(Credentials("User", "PaSS"))
        }

        "vault-like" {
            val creds =
                parse(
                    //language=JSON
                    """
                    { "data": { "username": "User", "password": "PaSS" } }
                    """.trimIndent(),
                )

            creds should beEqual(Credentials("User", "PaSS"))
        }

        "vault-like with extra fields" {
            val creds =
                parse(
                    //language=JSON
                    """
                    {
                      "data": {
                        "username": "User",
                        "password": "PaSS",
                        "sanity-level": 5
                      },
                      "list": [],
                      "obj": {"a":  1},
                      "str": "string"
                    }
                    """.trimIndent(),
                )

            creds should beEqual(Credentials("User", "PaSS"))
        }
    })
