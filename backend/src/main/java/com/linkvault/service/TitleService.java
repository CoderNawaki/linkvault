package com.linkvault.service;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class TitleService {

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(5))
      .followRedirects(HttpClient.Redirect.NEVER)
      .build();

  private static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  public String fetchTitle(String url) {
    try {
      URI uri = validatedFetchUri(url);
      if (uri == null) {
        return null;
      }

      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .timeout(Duration.ofSeconds(5))
          .header("User-Agent", "Mozilla/5.0")
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        Matcher matcher = TITLE_PATTERN.matcher(response.body());
        if (matcher.find()) {
          return matcher.group(1).trim();
        }
      }
    } catch (Exception e) {
      // Return null if fetching fails
    }
    return null;
  }

  URI validatedFetchUri(String url) {
    try {
      URI uri = URI.create(url).normalize();
      String scheme = uri.getScheme();
      String host = uri.getHost();

      if (scheme == null || host == null || host.isBlank() || uri.getUserInfo() != null) {
        return null;
      }

      String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
      if (!normalizedScheme.equals("http") && !normalizedScheme.equals("https")) {
        return null;
      }

      if (uri.getPort() < -1 || !isPublicHost(host)) {
        return null;
      }

      return uri;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private boolean isPublicHost(String host) {
    try {
      InetAddress[] addresses = InetAddress.getAllByName(host);
      if (addresses.length == 0) {
        return false;
      }

      for (InetAddress address : addresses) {
        if (isBlockedAddress(address)) {
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isBlockedAddress(InetAddress address) {
    if (address.isAnyLocalAddress()
        || address.isLoopbackAddress()
        || address.isLinkLocalAddress()
        || address.isSiteLocalAddress()
        || address.isMulticastAddress()) {
      return true;
    }

    byte[] bytes = address.getAddress();
    if (bytes.length == 4) {
      int first = Byte.toUnsignedInt(bytes[0]);
      int second = Byte.toUnsignedInt(bytes[1]);

      return first == 0
          || first == 10
          || first == 127
          || (first == 169 && second == 254)
          || (first == 172 && second >= 16 && second <= 31)
          || (first == 192 && second == 168)
          || first >= 224;
    }

    if (bytes.length == 16) {
      int first = Byte.toUnsignedInt(bytes[0]);
      return (first & 0xfe) == 0xfc;
    }

    return true;
  }
}
