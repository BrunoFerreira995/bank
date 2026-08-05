package com.brunopedraca.celcoin.pix;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.brunopedraca.celcoin.TestProperties;
import com.brunopedraca.celcoin.auth.CelcoinTokenService;
import com.brunopedraca.celcoin.common.http.CelcoinHttpClient;
import com.brunopedraca.celcoin.common.http.CelcoinWebClientFactory;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashInResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutAccountRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCashOutStaticQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixClaimListRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixClaimRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixCreditParty;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixDebitParty;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixDeleteKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixDueDateQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixDueDateSplitRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixImmediateSplitRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixKeyLookupResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixKeyRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixKeyResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixMerchant;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixMovementRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixPaymentStatusRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixQrCodeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixQrCodeResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixReceiptRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixRefundRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixRefundResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixScheduleListRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixScheduleRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixSplitFeeDetail;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixSplitFeeInfo;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixStaticChargeRequest;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixStatusResponse;
import com.brunopedraca.celcoin.pix.PixDtos.CelcoinPixUpdateKeyRequest;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class CelcoinPixClientWireMockTest {
    private WireMockServer wireMock;
    private CelcoinPixClient client;

    @BeforeEach
    void start() {
        wireMock = new WireMockServer(0);
        wireMock.start();
        CelcoinTokenService tokenService = mock(CelcoinTokenService.class);
        when(tokenService.getAccessToken()).thenReturn("test-token");
        WebClient webClient =
                CelcoinWebClientFactory.create(TestProperties.celcoin(wireMock.baseUrl()), true, tokenService);
        client = new CelcoinPixClient(new CelcoinHttpClient(webClient, TestProperties.celcoin(wireMock.baseUrl())));
    }

    @AfterEach
    void stop() {
        wireMock.stop();
    }

    @Test
    void createQrCodePostsToDynamicBrCodeEndpoint() {
        wireMock.stubFor(
                post(urlEqualTo("/pix/v1/brcode/dynamic"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                                                {"version":"1.0.0","status":201,"body":{"clientRequestId":"abc",
                                                "transactionId":"4000095510","transactionIdentification":"kk1",
                                                "body":{"key":"testepix@celcoin.com.br","location":"qrcode-h.pix",
                                                "dynamicBRCodeData":{"emvqrcps":"000201"}}}}""")));

        CelcoinPixQrCodeResponse response = client.createQrCode(
                new CelcoinPixQrCodeRequest(
                        "testepix@celcoin.com.br",
                        "10.00",
                        new CelcoinPixMerchant("5651", "06519435", "barueri", "Teste Celcoin"),
                        30000,
                        "client-1",
                        null,
                        null,
                        null,
                        null),
                "qr-1");

        assertThat(response.transactionId()).isEqualTo("4000095510");
        assertThat(response.emv()).isEqualTo("000201");
    }

    @Test
    void createStaticChargePostsToStaticBrCodeEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/pix/v1/brcode/static"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"transactionId\":\"tx-1\",\"emvqrcps\":\"000201static\","
                                + "\"transactionIdentification\":\"kk2\"}")));

        CelcoinPixCashInResponse response = client.createStaticChargeCashIn(
                new CelcoinPixStaticChargeRequest(
                        "testepix@celcoin.com.br",
                        new BigDecimal("10.00"),
                        new CelcoinPixMerchant("5651", "06519435", "barueri", "Teste"),
                        null),
                "static-1");

        assertThat(response.transactionId()).isEqualTo("tx-1");
        assertThat(response.qrCodeEmv()).isEqualTo("000201static");
    }

    @Test
    void createDueDateQrCodePostsToCollectionDuedateEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/pix/v1/collection/duedate"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"transactionId\":\"tx-2\",\"transactionIdentification\":\"kk3\","
                                + "\"status\":\"ACTIVE\"}")));

        CelcoinPixCashInResponse response = client.createDueDateQrCodeCashIn(
                new CelcoinPixDueDateQrCodeRequest(
                        "testepix@celcoin.com.br",
                        "client-2",
                        "15.63",
                        10,
                        OffsetDateTime.now().plusDays(1),
                        "12730587",
                        null,
                        null,
                        null,
                        null,
                        null),
                "due-1");

        assertThat(response.transactionId()).isEqualTo("tx-2");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void getStaticChargeQueriesStaticBrCodeById() {
        wireMock.stubFor(
                get(urlEqualTo("/pix/v1/brcode/static?transactionIdBrCode=761678748&transactionIdentification=kk2"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"transactionIdBrcode\":\"761678748\",\"key\":\"testepix@celcoin.com.br\","
                                        + "\"emvqrcps\":\"000201\",\"payments\":[{\"transactionId\":\"9001\","
                                        + "\"endToEnd\":\"E1393589320\",\"amount\":10.5}]}")));

        var response = client.getStaticCharge("761678748", "kk2");

        assertThat(response.transactionIdBrcode()).isEqualTo("761678748");
        assertThat(response.payments()).hasSize(1);
        assertThat(response.payments().getFirst().endToEnd()).isEqualTo("E1393589320");
    }

    @Test
    void getCashInReceiptQueriesReceivementStatus() {
        wireMock.stubFor(get(urlEqualTo("/pix/v2/receivement/v2/status?endtoEnd=E1393589320&transactionId=761679887"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\",\"body\":{\"transactionId\":\"761679887\","
                                + "\"amount\":118,\"endToEndId\":\"E1393589320\",\"initiationType\":\"MANUAL\"}}")));

        var response = client.getCashInReceipt(new CelcoinPixReceiptRequest("E1393589320", "761679887", null, null));

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.endToEndId()).isEqualTo("E1393589320");
        assertThat(response.amount()).isEqualTo(new BigDecimal("118"));
    }

    @Test
    void getMovementsQueriesWalletMovement() {
        wireMock.stubFor(get(urlEqualTo(
                        "/baas/v2/wallet/movement?Account=30054065526&DateFrom=2026-01-01&DateTo=2026-01-07&LimitPerPage=50&order=desc"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"body\":{\"movements\":[{\"id\":\"m1\",\"movementType\":\"PIXPAYMENTIN\","
                                + "\"amount\":10.5,\"status\":\"CONFIRMED\",\"endToEndId\":\"E1393589320\"}]}}")));

        var response = client.getMovements(new CelcoinPixMovementRequest(
                "30054065526", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 7), 50, null, "desc"));

        assertThat(response.movements()).hasSize(1);
        assertThat(response.movements().getFirst().movementType()).isEqualTo("PIXPAYMENTIN");
    }

    @Test
    void refundPostsToReverseEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas-wallet-transactions-webservice/v1/pix/reverse"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PROCESSING\",\"body\":{\"id\":\"rev-1\","
                                + "\"returnIdentification\":\"D1393589320\"}}")));
        CelcoinPixRefundResponse response = client.refund(
                new CelcoinPixRefundRequest("pay-1", "E1393589320", "code-1", new BigDecimal("15.00"), "BE08", null),
                "rev-1");

        assertThat(response.refundId()).isEqualTo("rev-1");
        assertThat(response.returnIdentification()).isEqualTo("D1393589320");
    }

    @Test
    void getRefundQueriesReverseStatus() {
        wireMock.stubFor(get(urlEqualTo(
                        "/baas-wallet-transactions-webservice/v1/pix/reverse/status?returnIdentification=D1393589320"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"COMPLETED\",\"body\":{\"id\":\"rev-1\","
                                + "\"returnIdentification\":\"D1393589320\"}}")));

        CelcoinPixRefundResponse response = client.getRefund("D1393589320");

        assertThat(response.status()).isEqualTo("COMPLETED");
        assertThat(response.returnIdentification()).isEqualTo("D1393589320");
    }

    @Test
    void getDevolutionQueriesReceivementDevolutionStatus() {
        wireMock.stubFor(get(urlEqualTo("/pix/v2/receivement/v2/devolution/status?returnIdentification=D1393589320"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\",\"returnIdentification\":\"D1393589320\","
                                + "\"originalEndToEndId\":\"E1393589320\",\"reason\":\"BE08\"}")));

        var response = client.getDevolution("D1393589320");

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.originalEndToEndId()).isEqualTo("E1393589320");
    }

    @Test
    void lookupKeyGetsDictExternalEntry() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/dict/entry/external/30054065526?key=77517432125"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SUCCESS\",\"body\":{\"keyType\":\"CPF\","
                                + "\"key\":\"77517432125\",\"endToEndId\":\"E1393589320\",\"isSameTaxId\":false}}}")));

        CelcoinPixKeyLookupResponse response = client.lookupKey("30054065526", "77517432125");

        assertThat(response.keyType()).isEqualTo("CPF");
        assertThat(response.endToEndId()).isEqualTo("E1393589320");
    }

    @Test
    void decodeEmvPostsToEmvFullEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/pix/v1/emv/full"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":200,\"body\":{\"type\":\"STATIC\","
                                + "\"key\":\"testepix@celcoin.com.br\",\"amount\":{\"original\":10.00},"
                                + "\"transactionIdentification\":\"kk2\"}}")));

        var response = client.decodeEmv("000201010212");

        assertThat(response.type()).isEqualTo("STATIC");
        assertThat(response.key()).isEqualTo("testepix@celcoin.com.br");
    }

    @Test
    void cashOutPostsToPaymentEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/payment"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PROCESSING\",\"body\":{\"id\":\"tx-3\","
                                + "\"endToEndId\":\"E1393589320\"}}}")));

        CelcoinPixPaymentResponse response = client.cashOut(
                new CelcoinPixPaymentRequest(
                        new BigDecimal("25.55"),
                        "client-3",
                        "MANUAL",
                        "IMMEDIATE",
                        "HIGH",
                        "TRANSFER",
                        null,
                        null,
                        "mensagem",
                        new CelcoinPixDebitParty("444444", null, null, null, null),
                        new CelcoinPixCreditParty(
                                "30306294", null, "10545584", "10545584", "11122233344", "Celcoin", "CACC"),
                        null),
                "pay-1");

        assertThat(response.transactionId()).isEqualTo("tx-3");
    }

    @Test
    void cashOutToAccountBuildsPaymentRequestAndPosts() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/payment"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PROCESSING\",\"body\":{\"id\":\"tx-4\","
                                + "\"endToEndId\":\"E1393589320\"}}}")));

        CelcoinPixPaymentResponse response = client.cashOutToAccount(
                new CelcoinPixCashOutAccountRequest(
                        "source-1", "1", "12345", "11122233344", "Recebedor", new BigDecimal("30.00"), "pgto", null),
                "pay-2");

        assertThat(response.transactionId()).isEqualTo("tx-4");
    }

    @Test
    void cashOutByKeyBuildsDictPaymentAndPosts() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/payment"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PROCESSING\",\"body\":{\"id\":\"tx-5\","
                                + "\"endToEndId\":\"E1393589320\"}}}")));

        CelcoinPixPaymentResponse response = client.cashOutByKey(
                new CelcoinPixCashOutKeyRequest(
                        "source-1", "77517432125", "30306294", "Celcoin", new BigDecimal("12.00"), "c-5", "msg"),
                "pay-3");

        assertThat(response.transactionId()).isEqualTo("tx-5");
    }

    @Test
    void cashOutStaticQrCodeDecodesLooksUpAndPays() {
        wireMock.stubFor(post(urlEqualTo("/pix/v1/emv/full"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"body\":{\"type\":\"STATIC\",\"key\":\"testepix@celcoin.com.br\","
                                + "\"amount\":{\"final\":9.90},\"transactionIdentification\":\"kk9\"}}")));
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/dict/entry/external/source-1?key=testepix%40celcoin.com.br"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"body\":{\"key\":\"testepix@celcoin.com.br\","
                                + "\"endToEndId\":\"E1393589320\",\"account\":{\"participant\":\"30306294\"},"
                                + "\"owner\":{\"name\":\"Celcoin\"}}}")));
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/payment"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"PROCESSING\",\"body\":{\"id\":\"tx-6\"}}}")));

        CelcoinPixPaymentResponse response = client.cashOutStaticQrCode(
                new CelcoinPixCashOutStaticQrCodeRequest("source-1", "000201", new BigDecimal("9.90"), "pgto qr"),
                "pay-4");

        assertThat(response.transactionId()).isEqualTo("tx-6");
    }

    @Test
    void getStatusQueriesPaymentStatus() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/payment/status?id=tx-3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\",\"body\":{\"id\":\"tx-3\"}}")));

        CelcoinPixStatusResponse response = client.getStatus("tx-3");

        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.transactionId()).isEqualTo("tx-3");
    }

    @Test
    void getPaymentStatusQueriesByIdAndClientCode() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/payment/status?id=tx-3&endtoendId=E1393589320&clientCode=c-3"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\",\"body\":{\"id\":\"tx-3\"}}")));

        var response = client.getPaymentStatus(new CelcoinPixPaymentStatusRequest("tx-3", "E1393589320", "c-3"));

        assertThat(response.status()).isEqualTo("CONFIRMED");
    }

    @Test
    void createKeyPostsToDictEntryEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/dict/entry"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SUCCESS\",\"body\":{\"keyType\":\"EMAIL\","
                                + "\"key\":\"testepix@celcoin.com.br\"}}")));

        CelcoinPixKeyResponse response =
                client.createKey(new CelcoinPixKeyRequest("30054065526", "EMAIL", "testepix@celcoin.com.br"), "key-1");

        assertThat(response.key()).isEqualTo("testepix@celcoin.com.br");
        assertThat(response.keyType()).isEqualTo("EMAIL");
    }

    @Test
    void listKeysGetsDictEntriesForAccount() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/dict/entry/30054065526"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SUCCESS\",\"body\":{\"listKeys\":[{\"keyType\":\"EMAIL\","
                                + "\"key\":\"testepix@celcoin.com.br\"}]}}")));

        var response = client.listKeys("30054065526");

        assertThat(response.listKeys()).hasSize(1);
        assertThat(response.listKeys().getFirst().key()).isEqualTo("testepix@celcoin.com.br");
    }

    @Test
    void deleteKeyDeletesDictEntryWithAccountBody() {
        wireMock.stubFor(delete(urlEqualTo("/baas/v2/pix/dict/entry/testepix%40celcoin.com.br"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SUCCESS\",\"version\":\"1.0.0\"}")));

        var response =
                client.deleteKey(new CelcoinPixDeleteKeyRequest("30054065526", "testepix@celcoin.com.br"), "del-1");

        assertThat(response.status()).isEqualTo("SUCCESS");
    }

    @Test
    void updateKeyNamePutsDictEntryWithAccount() {
        wireMock.stubFor(put(urlEqualTo("/baas/v2/pix/dict/entry"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"Version\":\"1.0.0\",\"Body\":{\"KeyType\":5,"
                                + "\"Key\":\"testepix@celcoin.com.br\"}}")));

        var response = client.updateKeyName(
                new CelcoinPixUpdateKeyRequest(
                        "30054065526", "testepix@celcoin.com.br", "Lavanderia 2 irmaos ltda", null),
                "upd-1");

        assertThat(response.key()).isEqualTo("testepix@celcoin.com.br");
    }

    @Test
    void createImmediateSplitPostsToImmediateSplitEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/immediate/split"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"transactionId\":\"tx-split-1\",\"status\":\"ACTIVE\","
                                + "\"transactionIdentification\":\"kk-split\"}")));

        var response = client.createImmediateSplitQrCode(
                new CelcoinPixImmediateSplitRequest(
                        "split-1",
                        "pergunta",
                        "testepix@celcoin.com.br",
                        null,
                        null,
                        null,
                        null,
                        null,
                        new CelcoinPixSplitFeeInfo(
                                10,
                                new BigDecimal("10.00"),
                                List.of(new CelcoinPixSplitFeeDetail(
                                        new BigDecimal("8.00"), "desc", "cc-1", "30054065526")))),
                "split-1");

        assertThat(response.transactionId()).isEqualTo("tx-split-1");
    }

    @Test
    void createDueDateSplitPostsToDuedateSplitEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/duedate/split"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"transactionId\":\"tx-split-2\",\"status\":\"ACTIVE\"}")));

        var response = client.createDueDateSplitQrCode(
                new CelcoinPixDueDateSplitRequest(
                        "split-2",
                        10,
                        OffsetDateTime.now().plusDays(1),
                        null,
                        null,
                        null,
                        new BigDecimal("18.00"),
                        null,
                        null,
                        null,
                        null,
                        "testepix@celcoin.com.br",
                        new CelcoinPixSplitFeeInfo(10, new BigDecimal("10.00"), List.of())),
                "split-2");

        assertThat(response.transactionId()).isEqualTo("tx-split-2");
    }

    @Test
    void schedulePostsScheduledPayment() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/payment"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CREATED\",\"body\":{\"scheduler\":{\"schedulerDate\":\"2026-08-10\","
                                + "\"schedulerId\":\"sched-1\"}}}")));

        var response = client.schedule(
                new CelcoinPixScheduleRequest(
                        new BigDecimal("0.01"),
                        "c-1",
                        "30054065526",
                        new CelcoinPixCreditParty("08561701", null, "305157919", "1", "06237998128", "Julio", "CACC"),
                        "2026-08-10",
                        "msg",
                        null),
                "sched-1");

        assertThat(response.status()).isEqualTo("CREATED");
        assertThat(response.schedulerId()).isEqualTo("sched-1");
    }

    @Test
    void getScheduleQueriesSchedulerById() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/scheduler/sched-1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SCHEDULED\",\"body\":{\"scheduler\":{\"schedulerDate\":\"2026-08-10\","
                                + "\"schedulerId\":\"sched-1\"}}}")));

        var response = client.getSchedule("sched-1");

        assertThat(response.status()).isEqualTo("SCHEDULED");
        assertThat(response.schedulerId()).isEqualTo("sched-1");
    }

    @Test
    void cancelScheduleDeletesSchedulerById() {
        wireMock.stubFor(delete(urlEqualTo("/baas/v2/scheduler/sched-1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CONFIRMED\",\"version\":\"1.0.0\"}")));

        var response = client.cancelSchedule("sched-1", "cancel-1");

        assertThat(response.status()).isEqualTo("CONFIRMED");
    }

    @Test
    void listSchedulesQueriesSchedulerListByAccount() {
        wireMock.stubFor(get(urlEqualTo(
                        "/baas/v2/scheduler/listByAccount/30054065526?DateFrom=2026-01-01&DateTo=2026-01-31&Page=1&LimitPerPage=20&Status=SCHEDULED"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"totalItems\":1,\"body\":{\"account\":\"30054065526\",\"scheduledList\":["
                                + "{\"scheduler\":{\"schedulerId\":\"sched-1\"},\"status\":\"SCHEDULED\"}]}}")));

        var response = client.listSchedules(new CelcoinPixScheduleListRequest(
                "30054065526", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 1, 20, "SCHEDULED"));

        assertThat(response.totalItems()).isEqualTo(1);
        assertThat(response.scheduledList()).hasSize(1);
    }

    @Test
    void claimKeyPostsToDictClaimEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/dict/claim"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OPEN\",\"body\":{\"id\":\"claim-1\",\"claimType\":\"PORTABILITY\","
                                + "\"key\":\"testepix@celcoin.com.br\"}}")));

        var response = client.claimKey(
                new CelcoinPixClaimRequest("testepix@celcoin.com.br", "EMAIL", "30054065526", "PORTABILITY"),
                "claim-1");

        assertThat(response.status()).isEqualTo("OPEN");
        assertThat(response.id()).isEqualTo("claim-1");
    }

    @Test
    void confirmClaimPostsToClaimConfirmEndpoint() {
        wireMock.stubFor(
                post(urlEqualTo("/baas/v2/pix/dict/claim/confirm"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\"status\":\"CONFIRMED\",\"body\":{\"id\":\"claim-1\",\"claimType\":\"OWNERSHIP\"}}")));

        var response = client.confirmClaim("claim-1", "USER_REQUESTED", "confirm-1");

        assertThat(response.status()).isEqualTo("CONFIRMED");
    }

    @Test
    void cancelClaimPostsToClaimCancelEndpoint() {
        wireMock.stubFor(post(urlEqualTo("/baas/v2/pix/dict/claim/cancel"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"CANCELLED\",\"body\":{\"id\":\"claim-1\"}}")));

        var response = client.cancelClaim("claim-1", "FRAUD", "cancel-1");

        assertThat(response.status()).isEqualTo("CANCELLED");
    }

    @Test
    void getClaimQueriesClaimById() {
        wireMock.stubFor(get(urlEqualTo("/baas/v2/pix/dict/claim/claim-1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OPEN\",\"body\":{\"id\":\"claim-1\",\"claimType\":\"OWNERSHIP\"}}")));

        var response = client.getClaim("claim-1");

        assertThat(response.id()).isEqualTo("claim-1");
    }

    @Test
    void listClaimsQueriesClaimList() {
        wireMock.stubFor(get(urlEqualTo(
                        "/baas/v2/pix/dict/claim/list?DateFrom=2026-01-01&DateTo=2026-01-31&Page=1&Status=OPEN&claimType=PORTABILITY"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"SUCCESS\",\"body\":{\"claims\":[{\"id\":\"claim-1\","
                                + "\"claimType\":\"PORTABILITY\"}]}}")));

        var response = client.listClaims(new CelcoinPixClaimListRequest(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null, 1, "OPEN", "PORTABILITY"));

        assertThat(response.claims()).hasSize(1);
        assertThat(response.claims().getFirst().id()).isEqualTo("claim-1");
    }

    @Test
    void participantsFetchesParticipantList() {
        wireMock.stubFor(get(urlEqualTo("/pix/v1/participants"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"ispb\":\"00315557\",\"name\":\"UNICRED\","
                                + "\"shortName\":\"UNICRED DO BRASIL\",\"type\":\"DRCT\"}]")));

        var participants = client.participants();

        assertThat(participants).hasSize(1);
        assertThat(participants.getFirst().ispb()).isEqualTo("00315557");
    }
}
