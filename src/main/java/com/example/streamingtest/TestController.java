package com.example.streamingtest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@Slf4j
public class TestController {

  // 신기하게도, 엑소플레이어의 hls 쪽에서도 이 메서드를 호출하는 것이 스트리밍이 가능했다.
  //  m3u8파일이 없었는데도 말이다.
  @GetMapping(value = "/not-hls-bad-performance")
  @ResponseBody
  public ResponseEntity<StreamingResponseBody> playMediaV01(
      @RequestHeader(value = "Range", required = false) String rangeHeader
  ) {
    try {
      StreamingResponseBody responseStream;
      String filePathString = "./test-long.mp4";
      Path filePath = Paths.get(filePathString);
      long fileSize = Files.size(filePath);
      byte[] buffer = new byte[1024];
      final HttpHeaders responseHeaders = new HttpHeaders();

      if (rangeHeader == null)
      {
        // 스트리밍을 처음 시작할 때는 이 메서드로 들어온다.
        responseHeaders.add("Content-Type", "video/mp4");
        responseHeaders.add("Content-Length", Long.toString(fileSize));
        responseStream = os -> {
          RandomAccessFile file = new RandomAccessFile(filePathString, "r");
          try (file) {
            long pos = 0;
            file.seek(pos);
            while (pos < fileSize - 1) {
              file.read(buffer);
              os.write(buffer);
              pos += buffer.length;
            }
            os.flush();
          } catch (Exception e) {
            e.printStackTrace();
          }
        };

        return new ResponseEntity<StreamingResponseBody>
            (responseStream, responseHeaders, HttpStatus.OK);
      }

      // 동영상 내에서 현재 재생 시각보다 더 과거 시점으로 탐색을 했을 때 이 부분으로 들어온다.
      String[] ranges = rangeHeader.split("-");

      Long rangeStart = Long.parseLong(ranges[0].substring(6));
      Long rangeEnd;
      if (ranges.length > 1)
      {
        rangeEnd = Long.parseLong(ranges[1]);
      }
      else
      {
        rangeEnd = fileSize - 1;
      }

      if (fileSize < rangeEnd)
      {
        rangeEnd = fileSize - 1;
      }

      String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
      responseHeaders.add("Content-Type", "video/mp4");
      responseHeaders.add("Content-Length", contentLength);
      responseHeaders.add("Accept-Ranges", "bytes");
      responseHeaders.add("Content-Range", "bytes" + " " +
          rangeStart + "-" + rangeEnd + "/" + fileSize);
      final Long _rangeEnd = rangeEnd;
      responseStream = os -> {
        RandomAccessFile file = new RandomAccessFile(filePathString, "r");
        try (file)
        {
          long pos = rangeStart;
          file.seek(pos);
          while (pos < _rangeEnd)
          {
            file.read(buffer);
            os.write(buffer);
            pos += buffer.length;
          }
          os.flush();
        }
        catch (Exception e) {}
      };

      return new ResponseEntity<StreamingResponseBody>
          (responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    } catch (FileNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } catch (IOException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @GetMapping(value = "/hls-test.m3u8")
  @ResponseBody
  public ResponseEntity<byte[]> getHlsM3U8File() throws IOException {

    Path m3u8Path = Path.of("./hls-videos/test.m3u8");

    byte[] contentByteArray = Files.readAllBytes(m3u8Path);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "audio/mpegurl")
        .header("Accept-Ranges", "bytes")
        .header("Content-Length", Integer.toString(contentByteArray.length))
        .body(contentByteArray);
  }

  @GetMapping(value = "/{tsFileName}")
  @ResponseBody
  public ResponseEntity<byte[]> getHlsTsFile(
      @PathVariable String tsFileName
  ) throws IOException {
    Path tsFilePath = Path.of("./hls-videos/" + tsFileName);

    byte[] contentByteArray = Files.readAllBytes(tsFilePath);

    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "audio/mpegurl")
        .header("Accept-Ranges", "bytes")
        .header("Content-Length", Integer.toString(contentByteArray.length))
        .body(contentByteArray);
  }
}






















