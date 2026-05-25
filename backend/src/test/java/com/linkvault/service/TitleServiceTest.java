package com.linkvault.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TitleServiceTest {

  private final TitleService titleService = new TitleService();

  @Test
  void acceptsPublicHttpAndHttpsUris() {
    assertThat(titleService.validatedFetchUri("https://93.184.216.34/docs")).isNotNull();
    assertThat(titleService.validatedFetchUri("http://93.184.216.34/docs")).isNotNull();
  }

  @Test
  void rejectsNonHttpProtocols() {
    assertThat(titleService.validatedFetchUri("file:///etc/passwd")).isNull();
    assertThat(titleService.validatedFetchUri("gopher://example.com")).isNull();
  }

  @Test
  void rejectsLocalAndPrivateAddresses() {
    assertThat(titleService.validatedFetchUri("http://127.0.0.1/admin")).isNull();
    assertThat(titleService.validatedFetchUri("http://10.0.0.1/admin")).isNull();
    assertThat(titleService.validatedFetchUri("http://172.16.0.1/admin")).isNull();
    assertThat(titleService.validatedFetchUri("http://192.168.1.1/admin")).isNull();
    assertThat(titleService.validatedFetchUri("http://169.254.169.254/latest/meta-data")).isNull();
    assertThat(titleService.validatedFetchUri("http://[::1]/admin")).isNull();
  }

  @Test
  void rejectsAmbiguousUris() {
    assertThat(titleService.validatedFetchUri("https://user@example.com")).isNull();
    assertThat(titleService.validatedFetchUri("https://")).isNull();
    assertThat(titleService.validatedFetchUri("not a url")).isNull();
  }
}
