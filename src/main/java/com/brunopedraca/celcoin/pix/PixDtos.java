package com.brunopedraca.celcoin.pix;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public final class PixDtos {
    private PixDtos() {}

    // ===================== QR Code e cobranças =====================

    public record CelcoinPixMerchant(String merchantCategoryCode, String postalCode, String city, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixQrCodeRequest(
            @NotBlank String key,
            String amount,
            CelcoinPixMerchant merchant,
            Integer expiration,
            String clientRequestId,
            String payerName,
            String payerCPF,
            String payerCNPJ,
            String payerQuestion) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixQrCodeResponse(String version, String status, CelcoinPixQrCodeBody body) {
        public String transactionId() {
            return body == null ? null : body.transactionId();
        }

        public String transactionIdentification() {
            return body == null ? null : body.transactionIdentification();
        }

        public String emv() {
            return body == null || body.body() == null || body.body().dynamicBRCodeData() == null
                    ? null
                    : body.body().dynamicBRCodeData().emvqrcps();
        }

        public String location() {
            return body == null || body.body() == null ? null : body.body().location();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixQrCodeBody(
                String clientRequestId,
                String pactualId,
                String transactionId,
                String createTimestamp,
                String lastUpdateTimestamp,
                String entity,
                String status,
                List<Object> tags,
                String transactionIdentification,
                CelcoinPixDynamicBrCodeData body) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixDynamicBrCodeData(
                String key,
                String revision,
                String location,
                Object debtor,
                Object amount,
                Object calendar,
                CelcoinPixBrCodePayload dynamicBRCodeData,
                Object additionalInformation) {}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixBrCodePayload(
                String pointOfInitiationMethod,
                String payloadFormatIndicator,
                String countryCode,
                String merchantName,
                String merchantCity,
                String transactionIdentification,
                String transactionAmount,
                String emvqrcps,
                Integer merchantCategoryCode,
                Integer transactionCurrency,
                Object merchantAccountInformation) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixStaticChargeRequest(
            @NotBlank String key, BigDecimal amount, CelcoinPixMerchant merchant, String transactionIdentification) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDueDateQrCodeRequest(
            @NotBlank String key,
            @NotBlank String clientRequestId,
            String amount,
            Integer expirationAfterPayment,
            OffsetDateTime duedate,
            String locationId,
            CelcoinPixPerson debtor,
            CelcoinPixPerson receiver,
            CelcoinPixDiscount discount,
            CelcoinPixFine fine,
            CelcoinPixInterest interest) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixPerson(
            String name,
            String fantasyName,
            String cpf,
            String cnpj,
            String publicArea,
            String city,
            String state,
            String postalCode,
            String email) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDiscount(
            Boolean hasDiscount, String modality, List<CelcoinPixDateValue> discountDateFixed) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixFine(Boolean hasFine, String amountPerc, String modality) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixInterest(Boolean hasInterest, String amountPerc, String modality) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDateValue(String date, String amountPerc) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixCashInResponse(
            String transactionId,
            String transactionIdentification,
            String status,
            @JsonAlias("emvqrcps") String emv,
            CelcoinPixQrCodeLocation location,
            Map<String, Object> raw) {
        public String qrCodeEmv() {
            if (emv != null) {
                return emv;
            }
            return location == null ? null : location.emv();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixQrCodeLocation(
            String url, String emv, String type, String locationId, CelcoinPixMerchant merchant) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixStaticChargeResponse(
            String key,
            BigDecimal amount,
            String transactionIdBrcode,
            String brCodeId,
            String createdAt,
            String updatedAt,
            String transactionIdentification,
            String partnerNumber,
            Integer quantityPayments,
            BigDecimal amountPayments,
            String additionalInformation,
            String emvqrcps,
            List<CelcoinPixPaymentInfo> payments) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixPaymentInfo(
            String transactionId,
            String endToEnd,
            String createdAt,
            BigDecimal amount,
            String payerName,
            String payerTaxId,
            CelcoinPixParty creditParty,
            CelcoinPixParty debitParty) {}

    // ===================== Recebimentos / extrato =====================

    public record CelcoinPixReceiptRequest(
            String endToEndId, String transactionId, String transactionIdBrCode, String clientRequestId) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixReceiptResponse(String status, String version, CelcoinPixReceiptBody body) {
        public String endToEndId() {
            return body == null ? null : body.endToEndId();
        }

        public String transactionId() {
            return body == null ? null : body.transactionId();
        }

        public BigDecimal amount() {
            return body == null ? null : body.amount();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixReceiptBody(
                String transactionType,
                String transactionId,
                BigDecimal amount,
                CelcoinPixParty debitParty,
                CelcoinPixParty creditParty,
                String endToEndId,
                String transactionIdentification,
                String transactionIdBRCode,
                String initiationType,
                String transactionTypePix,
                String paymentType,
                String urgency,
                String createTimestamp,
                String clientRequestId,
                String status) {}
    }

    public record CelcoinPixMovementRequest(
            @NotBlank String account,
            LocalDate dateFrom,
            LocalDate dateTo,
            Integer limitPerPage,
            Boolean additionalInformation,
            String order) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixMovementResponse(String version, String status, CelcoinPixMovementBody body) {
        public List<CelcoinPixMovement> movements() {
            return body == null ? null : body.movements();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixMovementBody(List<CelcoinPixMovement> movements) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixMovement(
            String id,
            String clientCode,
            String description,
            String createDate,
            String lastUpdateDate,
            String confirmationDate,
            BigDecimal amount,
            String status,
            String balanceType,
            String movementType,
            String nameDebit,
            String nameCredit,
            String accountDebit,
            String accountCredit,
            String endToEndId,
            String reasonDevolution,
            String returnIdentification,
            String originalEndToEndId) {}

    // ===================== Decodificação de EMV =====================

    public record CelcoinPixEmvDecodeRequest(String emv, String codMun, String dpp) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixEmvDecodeResponse(String version, String status, CelcoinPixEmvBody body) {
        public String type() {
            return body == null ? null : body.type();
        }

        public String key() {
            return body == null ? null : body.key();
        }

        public String transactionIdentification() {
            return body == null ? null : body.transactionIdentification();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixEmvBody(
                String type,
                CelcoinPixEmvMerchantInfo merchantAccountInformation,
                String key,
                CelcoinPixEmvAmount amount,
                String transactionIdentification,
                CelcoinPixEmvPayload payload) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixEmvMerchantInfo(
            String url,
            String gui,
            String merchantCategoryCode,
            String additionaldata,
            String withdrawalServiceProvider,
            String merchantName,
            String merchantCity,
            String postalCode) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixEmvAmount(
            BigDecimal original,
            BigDecimal abatement,
            BigDecimal discount,
            BigDecimal interest,
            @JsonAlias("final") BigDecimal finalAmount,
            BigDecimal fine,
            Boolean canModifyFinalAmount,
            Map<String, Object> withdrawal,
            Map<String, Object> change) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixEmvPayload(
            String status,
            Integer revision,
            CelcoinPixEmvCalendar calendar,
            CelcoinPixPerson debtor,
            Object receiver,
            String payerQuestion,
            List<Map<String, String>> additionalInformation) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixEmvCalendar(
            String createdAt,
            String presentation,
            String dueDate,
            String validateAfterDuedate,
            Integer expiration,
            String expirationDate) {}

    // ===================== Cash-in webhook =====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixCashInWebhookEvent(
            String createTimeStamp,
            String entity,
            String status,
            String id,
            BigDecimal amount,
            BigDecimal currentBalance,
            BigDecimal oldBalance,
            String endToEndId,
            String paymentType,
            String initiationType,
            String transactionType,
            String urgency,
            String remittanceInformation,
            String transactionIdentification,
            String reason,
            BigDecimal blockedAmount,
            String expireDate,
            CelcoinPixParty debitParty,
            CelcoinPixParty creditParty,
            Map<String, Object> raw) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixCashOutWebhookEvent(
            String createTimeStamp,
            String entity,
            String status,
            String id,
            String clientCode,
            BigDecimal amount,
            BigDecimal currentBalance,
            BigDecimal oldBalance,
            String endToEndId,
            String paymentType,
            String initiationType,
            String transactionType,
            String urgency,
            String remittanceInformation,
            String transactionIdentification,
            String reason,
            CelcoinPixError error,
            CelcoinPixParty debitParty,
            CelcoinPixParty creditParty,
            Map<String, Object> raw) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixError(String errorCode, String message) {}

    // ===================== Cash-out / pagamentos =====================

    public record CelcoinPixParty(
            String bank, String key, String account, String branch, String taxId, String name, String accountType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixPaymentRequest(
            BigDecimal amount,
            @NotBlank String clientCode,
            @NotBlank String initiationType,
            @NotBlank String paymentType,
            @NotBlank String urgency,
            @NotBlank String transactionType,
            String transactionIdentification,
            String endToEndId,
            String remittanceInformation,
            @NotBlank CelcoinPixDebitParty debitParty,
            @NotBlank CelcoinPixCreditParty creditParty,
            CelcoinPixScheduler scheduler) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixScheduler(String schedulerDate) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDebitParty(
            @NotBlank String account, String branch, String taxId, String name, String accountType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixCreditParty(
            @NotBlank String bank,
            String key,
            String account,
            String branch,
            String taxId,
            String name,
            @NotBlank String accountType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixPaymentResponse(String status, String version, CelcoinPixPaymentBody body) {
        public String transactionId() {
            return body == null ? null : body.id();
        }

        public String endToEndId() {
            return body == null ? null : body.endToEndId();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixPaymentBody(
                String id,
                BigDecimal amount,
                String clientCode,
                String transactionIdentification,
                String endToEndId,
                String initiationType,
                String paymentType,
                String urgency,
                String transactionType,
                CelcoinPixParty debitParty,
                CelcoinPixParty creditParty,
                String remittanceInformation,
                CelcoinPixError error) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixStatusResponse(String status, String version, CelcoinPixStatusBody body) {
        public String transactionId() {
            return body == null ? null : body.id();
        }

        public String endToEndId() {
            return body == null ? null : body.endToEndId();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixStatusBody(
                String id,
                BigDecimal amount,
                String clientCode,
                String transactionIdentification,
                String endToEndId,
                String initiationType,
                String paymentType,
                String urgency,
                String transactionType,
                CelcoinPixParty debitParty,
                CelcoinPixParty creditParty,
                String remittanceInformation,
                CelcoinPixError error) {}
    }

    public record CelcoinPixPaymentStatusRequest(String id, String endToEndId, String clientCode) {}

    public record CelcoinPixCashOutAccountRequest(
            @NotBlank String sourceAccountId,
            @NotBlank String targetBranch,
            @NotBlank String targetAccount,
            String targetDocument,
            @NotBlank String targetName,
            BigDecimal amount,
            String description,
            Map<String, Object> metadata) {}

    public record CelcoinPixCashOutKeyRequest(
            @NotBlank String sourceAccountId,
            @NotBlank String key,
            String bank,
            String name,
            BigDecimal amount,
            String clientCode,
            String remittanceInformation) {}

    public record CelcoinPixCashOutStaticQrCodeRequest(
            @NotBlank String accountId, @NotBlank String emv, BigDecimal amount, String description) {}

    public record CelcoinPixCashOutDynamicQrCodeRequest(
            @NotBlank String accountId, @NotBlank String emv, String description) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDevolutionStatusResponse(
            String status,
            String returnIdentification,
            String additionalInformation,
            String originalEndToEndId,
            String transactionId,
            String transactionIdPayment,
            String transactionType,
            BigDecimal amount,
            String reason,
            String reversalDescription,
            String createdAt) {}

    // ===================== DICT / chaves =====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyLookupResponse(String status, String version, CelcoinPixKeyBody body) {
        public String key() {
            return body == null ? null : body.key();
        }

        public String keyType() {
            return body == null ? null : body.keyType();
        }

        public String endToEndId() {
            return body == null ? null : body.endToEndId();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixKeyBody(
                String keyType,
                String key,
                CelcoinPixKeyAccount account,
                CelcoinPixKeyOwner owner,
                String endToEndId,
                Boolean isSameTaxId,
                Map<String, Object> statistics) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyAccount(
            String participant,
            String branch,
            String account,
            String accountType,
            String createDate,
            @JsonAlias("taxIdNumber") String taxIdNumber) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyOwner(
            String type,
            String documentNumber,
            @JsonAlias("taxIdNumber") String taxIdNumber,
            String name,
            String tradeName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyRequest(@NotBlank String account, @NotBlank String keyType, String key) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyResponse(String status, String version, CelcoinPixKeyBody body) {
        public String key() {
            return body == null ? null : body.key();
        }

        public String keyType() {
            return body == null ? null : body.keyType();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixKeyBody(
                String keyType,
                String key,
                CelcoinPixKeyAccount account,
                CelcoinPixKeyOwner owner,
                String endToEndId,
                Boolean isSameTaxId) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyListResponse(String status, String version, CelcoinPixKeyListBody body) {
        public List<CelcoinPixKeyItem> listKeys() {
            return body == null ? null : body.listKeys();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixKeyListBody(List<CelcoinPixKeyItem> listKeys) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyItem(
            String keyType, String key, CelcoinPixKeyAccount account, CelcoinPixKeyOwner owner) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyOperationResponse(String version, String status, Map<String, Object> body) {}

    public record CelcoinPixDeleteKeyRequest(@NotBlank String account, @NotBlank String key) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixUpdateKeyRequest(
            @NotBlank String account, @NotBlank String key, String name, String tradeName) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixKeyUpdateResponse(
            @JsonAlias("Status") String status,
            @JsonAlias("Version") String version,
            @JsonAlias("Body") CelcoinPixKeyUpdateBody body) {
        public String key() {
            return body == null ? null : body.key();
        }

        public String keyType() {
            return body == null ? null : body.keyType();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixKeyUpdateBody(
                @JsonAlias("KeyType") String keyType,
                @JsonAlias("Key") String key,
                @JsonAlias("Account") CelcoinPixKeyAccount account,
                @JsonAlias("Owner") CelcoinPixKeyOwner owner) {}
    }

    // ===================== Split =====================

    public record CelcoinPixSplitFeeInfo(
            Integer percent, BigDecimal totalAmount, List<CelcoinPixSplitFeeDetail> feeDetails) {}

    public record CelcoinPixSplitFeeDetail(
            BigDecimal amount, String description, String clientRequestId, String accountCredit) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixImmediateSplitRequest(
            String clientRequestId,
            String payerQuestion,
            String key,
            String locationId,
            CelcoinPixPerson debtor,
            Map<String, Object> amount,
            Map<String, Object> calendar,
            List<Map<String, Object>> additionalInformation,
            CelcoinPixSplitFeeInfo feeInfo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDueDateSplitRequest(
            String clientRequestId,
            Integer expirationAfterPayment,
            OffsetDateTime duedate,
            CelcoinPixPerson debtor,
            CelcoinPixPerson receiver,
            String locationId,
            BigDecimal amount,
            Map<String, Object> amountAbatement,
            Map<String, Object> amountFine,
            Map<String, Object> amountInterest,
            String payerQuestion,
            String key,
            CelcoinPixSplitFeeInfo feeInfo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixSplitResponse(
            String transactionIdentification,
            String transactionId,
            String clientRequestId,
            String status,
            BigDecimal amount,
            CelcoinPixQrCodeLocation location,
            String key,
            CelcoinPixSplitFeeInfo feeInfo) {}

    // ===================== Devolução =====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixRefundRequest(
            @NotBlank String id,
            @NotBlank String endToEndId,
            @NotBlank String clientCode,
            BigDecimal amount,
            @NotBlank String reason,
            String reversalDescription) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixRefundResponse(String status, String version, CelcoinPixRefundBody body) {
        public String refundId() {
            return body == null ? null : body.id();
        }

        public String returnIdentification() {
            return body == null ? null : body.returnIdentification();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixRefundBody(
                String id,
                String returnIdentification,
                String originalPaymentId,
                BigDecimal amount,
                String reason,
                String endToEndId) {}
    }

    // ===================== Participantes =====================

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixParticipantResponse(
            String ispb, String name, String shortName, String type, String startOperationDatetime) {}

    // ===================== Agendamento =====================

    public record CelcoinPixScheduleRequest(
            BigDecimal amount,
            @NotBlank String clientCode,
            @NotBlank String debitAccount,
            @NotBlank CelcoinPixCreditParty creditParty,
            @NotBlank String schedulerDate,
            String remittanceInformation,
            String transactionIdentification) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixScheduleResponse(String status, String version, CelcoinPixScheduleBody body) {
        public String schedulerId() {
            return body == null || body.scheduler() == null
                    ? null
                    : body.scheduler().schedulerId();
        }

        public String schedulerDate() {
            return body == null || body.scheduler() == null
                    ? null
                    : body.scheduler().schedulerDate();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixScheduleBody(
                String id,
                BigDecimal amount,
                String clientCode,
                String initiationType,
                String transactionType,
                String paymentType,
                String urgency,
                String createAt,
                String updateAt,
                String remittanceInformation,
                String status,
                CelcoinPixParty debitParty,
                CelcoinPixParty creditParty,
                CelcoinPixScheduleInfo scheduler,
                CelcoinPixError error) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixScheduleInfo(String schedulerId, String schedulerDate, String product, String source) {}

    public record CelcoinPixScheduleListRequest(
            String account, LocalDate dateFrom, LocalDate dateTo, Integer page, Integer limitPerPage, String status) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixScheduleListResponse(
            Integer totalItems,
            Integer currentPage,
            Integer limitPerPage,
            Integer totalPages,
            String dateFrom,
            String dateTo,
            String version,
            String status,
            CelcoinPixScheduleListBody body) {
        public List<CelcoinPixScheduleResponse.CelcoinPixScheduleBody> scheduledList() {
            return body == null ? null : body.scheduledList();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixScheduleListBody(
                String account, List<CelcoinPixScheduleResponse.CelcoinPixScheduleBody> scheduledList) {}
    }

    // ===================== Portabilidade / reivindicação =====================

    public record CelcoinPixClaimRequest(
            @NotBlank String key, @NotBlank String keyType, @NotBlank String account, @NotBlank String claimType) {}

    public record CelcoinPixClaimActionRequest(@NotBlank String id, String reason) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixClaimResponse(String version, String status, CelcoinPixClaimBody body) {
        public String id() {
            return body == null ? null : body.id();
        }

        public String claimType() {
            return body == null ? null : body.claimType();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixClaimBody(
                String id,
                String claimType,
                String key,
                String keyType,
                CelcoinPixClaimerAccount claimerAccount,
                CelcoinPixClaimer claimer,
                String donorParticipant,
                String createTimestamp,
                String completionPeriodEnd,
                String resolutionPeriodEnd,
                String lastModified,
                String confirmReason,
                String cancelReason,
                String cancelledBy,
                CelcoinPixDonorAccount donorAccount) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixClaimerAccount(String participant, String branch, String account, String accountType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixClaimer(String personType, String taxId, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixDonorAccount(String account, String branch, String taxId, String name) {}

    public record CelcoinPixClaimListRequest(
            LocalDate dateFrom,
            LocalDate dateTo,
            Integer limitPerPage,
            Integer page,
            String status,
            String claimType) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CelcoinPixClaimListResponse(String version, String status, CelcoinPixClaimListBody body) {
        public List<CelcoinPixClaimResponse.CelcoinPixClaimBody> claims() {
            return body == null ? null : body.claims();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CelcoinPixClaimListBody(List<CelcoinPixClaimResponse.CelcoinPixClaimBody> claims) {}
    }
}
