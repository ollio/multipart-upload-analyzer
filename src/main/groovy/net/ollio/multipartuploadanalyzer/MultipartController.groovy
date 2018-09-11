package net.ollio.multipartuploadanalyzer

import groovy.util.logging.Log
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import java.security.MessageDigest

@Log
@RestController
class MultipartController {

  @PostMapping(value = "/**",
      produces = [ "application/json" ],
      consumes = [ "multipart/form-data" ])
  ResponseEntity<Result> uploadDokument(@Valid @RequestPart("file") MultipartFile content,
                                                     @RequestParam(value="anzeigename", required=false)  String anzeigename,
                                                     @RequestParam(value="vorgangsNummer", required=false)  String vorgangsNummer,
                                        HttpServletRequest request) {

    MessageDigest md = MessageDigest.getInstance("MD5")
    def digest = Base64.encoder.encodeToString(md.digest(content.bytes))

    return ResponseEntity.ok(new Result(
      requestURI: request.getRequestURI(),
      headers: request.headerNames.iterator().collectEntries {
        [it, request.getHeader(it)]
      },
      contentSize: content.bytes.size(),
      contentMd5: digest,
      contentType: content.contentType,
      contentFileName: content.getOriginalFilename(),
      contentName: content.name,
      anzeigename: anzeigename,
      vorgangsNummer: vorgangsNummer
    ))
  }

  class Result {
    long contentSize
    String contentMd5
    String requestURI
    String contentType
    String contentFileName
    String contentName
    String anzeigename
    String vorgangsNummer
    Map headers
  }
}
