Kithub
======

This is a Kotlin library for github interaction.

How
===

If you want to use the API you need to convert the key you got from github.

```
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in <the_key_you_got_from_github> -out <the_key_to_use_with_this_library>
```

Get it
======

Kithub is available via jitpack:
[![](https://jitpack.io/v/ligi/kithub.svg)](https://jitpack.io/#ligi/kithub)
