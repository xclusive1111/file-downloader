### Simple File Downloader

#### Build
   * Generate application secret: 

```sh
$ sbt playGenerateSecret
```

   * Build:

```sh
$ sbt dist
```

   * Unzip file in `target/univesal`
   * Start application:

```sh
$ bin/file-downloader -Dplay.http.secret.key='<your_secret>' -Dhttp.port=8080
```

