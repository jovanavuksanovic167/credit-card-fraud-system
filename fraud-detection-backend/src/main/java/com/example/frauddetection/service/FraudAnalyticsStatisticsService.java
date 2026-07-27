package com.example.frauddetection.service;

import com.example.frauddetection.dto.FraudAnalyticsStatisticsResponse;
import com.example.frauddetection.model.CaseStatus;
import com.example.frauddetection.model.FraudCase;
import com.example.frauddetection.model.Transaction;
import com.example.frauddetection.repository.FraudCaseRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FraudAnalyticsStatisticsService {

    private final FraudCaseRepository fraudCaseRepository;

    public FraudAnalyticsStatisticsService(FraudCaseRepository fraudCaseRepository) {
        this.fraudCaseRepository = fraudCaseRepository;
    }

    public FraudAnalyticsStatisticsResponse getStatistics() {
        List<FraudCase> cases = fraudCaseRepository.findAll();

        List<FraudCase> confirmedCases = cases.stream()
                .filter(fraudCase -> fraudCase.getStatus() == CaseStatus.CONFIRMED_FRAUD)
                .toList();

        List<FraudCase> falseAlertCases = cases.stream()
                .filter(fraudCase -> fraudCase.getStatus() == CaseStatus.FALSE_ALERT)
                .toList();

        FraudAnalyticsStatisticsResponse response = new FraudAnalyticsStatisticsResponse();

        response.setTotalCases(cases.size());
        response.setNewCases(countByStatus(cases, CaseStatus.NEW));
        response.setInReviewCases(countByStatus(cases, CaseStatus.IN_REVIEW));
        response.setConfirmedFraudCases(countByStatus(cases, CaseStatus.CONFIRMED_FRAUD));
        response.setFalseAlertCases(countByStatus(cases, CaseStatus.FALSE_ALERT));

        response.setPreventedLoss(sumAmount(confirmedCases));
        response.setAverageConfirmedFraudAmount(averageAmount(confirmedCases));
        response.setHighestConfirmedFraudAmount(highestAmount(confirmedCases));
        response.setAverageFalseAlertAmount(averageAmount(falseAlertCases));

        response.setPinNotUsed(countBinaryValue(cases, "usedPinNumber", 0));
        response.setChipNotUsed(countBinaryValue(cases, "usedChip", 0));
        response.setOnlineOrders(countBinaryValue(cases, "onlineOrder", 1));
        response.setNewRetailers(countBinaryValue(cases, "repeatRetailer", 0));

        response.setAverageDistanceFromHome(averageDistanceFromHome(cases));
        response.setAverageDistanceFromLastTransaction(averageDistanceFromLastTransaction(cases));
        response.setAveragePurchasePriceRatio(averagePurchasePriceRatio(cases));

        response.setCaseStatusDistribution(caseStatusDistribution(cases));

        response.setUsedPinDistribution(binaryDistribution(cases, "usedPinNumber"));
        response.setUsedChipDistribution(binaryDistribution(cases, "usedChip"));
        response.setOnlineOrderDistribution(binaryDistribution(cases, "onlineOrder"));
        response.setRepeatRetailerDistribution(binaryDistribution(cases, "repeatRetailer"));

        response.setDistanceFromHomeCategories(distanceFromHomeCategories(cases));
        response.setDistanceFromLastTransactionCategories(distanceFromLastTransactionCategories(cases));
        response.setPurchasePriceRatioCategories(purchasePriceRatioCategories(cases));

        return response;
    }

    private long countByStatus(List<FraudCase> cases, CaseStatus status) {
        return cases.stream()
                .filter(fraudCase -> fraudCase.getStatus() == status)
                .count();
    }

    private Transaction getTransaction(FraudCase fraudCase) {
        return fraudCase.getTransaction();
    }

    private double getAmount(FraudCase fraudCase) {
        Double amount = getTransaction(fraudCase).getAmount();
        return amount == null ? 0.0 : amount;
    }

    private double sumAmount(List<FraudCase> cases) {
        return cases.stream()
                .mapToDouble(this::getAmount)
                .sum();
    }

    private double averageAmount(List<FraudCase> cases) {
        if (cases.isEmpty()) {
            return 0.0;
        }

        return sumAmount(cases) / cases.size();
    }

    private double highestAmount(List<FraudCase> cases) {
        return cases.stream()
                .mapToDouble(this::getAmount)
                .max()
                .orElse(0.0);
    }

    private Integer getBinaryValue(Transaction transaction, String field) {
        return switch (field) {
            case "usedPinNumber" -> transaction.getUsedPinNumber();
            case "usedChip" -> transaction.getUsedChip();
            case "onlineOrder" -> transaction.getOnlineOrder();
            case "repeatRetailer" -> transaction.getRepeatRetailer();
            default -> null;
        };
    }

    private long countBinaryValue(List<FraudCase> cases, String field, int expectedValue) {
        return cases.stream()
                .map(FraudCase::getTransaction)
                .filter(transaction -> getBinaryValue(transaction, field) != null)
                .filter(transaction -> getBinaryValue(transaction, field) == expectedValue)
                .count();
    }

    private Map<String, Long> binaryDistribution(List<FraudCase> cases, String field) {
        Map<String, Long> result = new LinkedHashMap<>();

        result.put("Yes", 0L);
        result.put("No", 0L);

        for (FraudCase fraudCase : cases) {
            Integer value = getBinaryValue(getTransaction(fraudCase), field);

            if (value == null) {
                continue;
            }

            if (value == 1) {
                result.put("Yes", result.get("Yes") + 1);
            } else {
                result.put("No", result.get("No") + 1);
            }
        }

        return result;
    }

    private double averageDistanceFromHome(List<FraudCase> cases) {
        return cases.stream()
                .map(FraudCase::getTransaction)
                .filter(transaction -> transaction.getDistanceFromHome() != null)
                .mapToDouble(Transaction::getDistanceFromHome)
                .average()
                .orElse(0.0);
    }

    private double averageDistanceFromLastTransaction(List<FraudCase> cases) {
        return cases.stream()
                .map(FraudCase::getTransaction)
                .filter(transaction -> transaction.getDistanceFromLastTransaction() != null)
                .mapToDouble(Transaction::getDistanceFromLastTransaction)
                .average()
                .orElse(0.0);
    }

    private double averagePurchasePriceRatio(List<FraudCase> cases) {
        return cases.stream()
                .map(FraudCase::getTransaction)
                .filter(transaction -> transaction.getRatioToMedianPurchasePrice() != null)
                .mapToDouble(Transaction::getRatioToMedianPurchasePrice)
                .average()
                .orElse(0.0);
    }

    private Map<String, Long> caseStatusDistribution(List<FraudCase> cases) {
        Map<String, Long> result = new LinkedHashMap<>();

        result.put("New", countByStatus(cases, CaseStatus.NEW));
        result.put("In review", countByStatus(cases, CaseStatus.IN_REVIEW));
        result.put("Confirmed fraud", countByStatus(cases, CaseStatus.CONFIRMED_FRAUD));
        result.put("False alert", countByStatus(cases, CaseStatus.FALSE_ALERT));

        return result;
    }

    private Map<String, Long> distanceFromHomeCategories(List<FraudCase> cases) {
        Map<String, Long> result = createDistanceMap();

        for (FraudCase fraudCase : cases) {
            Double value = getTransaction(fraudCase).getDistanceFromHome();

            if (value == null) {
                continue;
            }

            String category = distanceCategory(value);
            result.put(category, result.get(category) + 1);
        }

        return result;
    }

    private Map<String, Long> distanceFromLastTransactionCategories(List<FraudCase> cases) {
        Map<String, Long> result = createDistanceMap();

        for (FraudCase fraudCase : cases) {
            Double value = getTransaction(fraudCase).getDistanceFromLastTransaction();

            if (value == null) {
                continue;
            }

            String category = distanceCategory(value);
            result.put(category, result.get(category) + 1);
        }

        return result;
    }

    private Map<String, Long> purchasePriceRatioCategories(List<FraudCase> cases) {
        Map<String, Long> result = new LinkedHashMap<>();

        result.put("Lower than usual", 0L);
        result.put("Similar to usual", 0L);
        result.put("Higher than usual", 0L);
        result.put("Much higher than usual", 0L);

        for (FraudCase fraudCase : cases) {
            Double value = getTransaction(fraudCase).getRatioToMedianPurchasePrice();

            if (value == null) {
                continue;
            }

            String category = purchaseRatioCategory(value);
            result.put(category, result.get(category) + 1);
        }

        return result;
    }

    private Map<String, Long> createDistanceMap() {
        Map<String, Long> result = new LinkedHashMap<>();

        result.put("Close", 0L);
        result.put("Moderate", 0L);
        result.put("Far", 0L);
        result.put("Very far", 0L);

        return result;
    }

    private String distanceCategory(double value) {
        if (value < 1) {
            return "Close";
        }

        if (value < 10) {
            return "Moderate";
        }

        if (value < 50) {
            return "Far";
        }

        return "Very far";
    }

    private String purchaseRatioCategory(double value) {
        if (value < 1) {
            return "Lower than usual";
        }

        if (value < 3) {
            return "Similar to usual";
        }

        if (value < 10) {
            return "Higher than usual";
        }

        return "Much higher than usual";
    }
}