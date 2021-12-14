package vendingmachine;

import camp.nextstep.edu.missionutils.Randoms;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VendingMachine {

    Map<Coin, Integer> coinBox;
    List<Product> products;
    Speaker speaker;
    Touchpad touchpad;

    public VendingMachine() {
        this.coinBox = new LinkedHashMap<>();
        this.speaker = new Speaker();
        this.touchpad = new Touchpad();
        List<Coin> sortedCoins = Arrays.stream(Coin.values()).sorted().collect(Collectors.toList());
        for (Coin coin : sortedCoins) {
            coinBox.put(coin, 0);
        }
    }

    public void setBaseAsset() {
        speaker.requestVendingMachineAsset();
        int asset = touchpad.insertVendingMachineAsset();
        generateRandomCoinBasedOnAsset(asset);
        speaker.announceCoinStock(coinBox);
    }

    public void setProducts() {
        products = new ArrayList<>();
        speaker.requestProducts();
        List<String> rawProducts = touchpad.inputProducts();
        ;
        collectProducts(rawProducts);
    }

    public void sellProducts() {
        speaker.announceInsertMoney();
        int insertedMoney = touchpad.insertMoney();
        while (true) {
            speaker.announceBalance(insertedMoney);
            if (!purchasePossible(insertedMoney)) {
                speaker.announceChanges();
                changeMoney(insertedMoney);
                break;
            }
            speaker.requestPurchaseProduct();
            String wantedProduct = touchpad.purchaseProduct();
            insertedMoney -= sellProduct(wantedProduct);
        }
    }

    private void generateRandomCoinBasedOnAsset(int asset) {
        Set<Coin> coins = coinBox.keySet();
        while (asset > 0) {
            for (Coin coin : coins) {
                int amount = coin.getCoinValue();
                int maxCoinCount = asset / amount;
                List<Integer> availableNumbers = makeAvailableNumbers(maxCoinCount);
                int randomCoinCount = Randoms.pickNumberInList(availableNumbers);
                asset -= amount * randomCoinCount;
                coinBox.put(coin, randomCoinCount + coinBox.get(coin));
            }
        }
    }

    private List<Integer> makeAvailableNumbers(int maxCoinCount) {
        List<Integer> tempNumbers = new ArrayList<>();
        for (int i = 0; i <= maxCoinCount; i++) {
            tempNumbers.add(i);
        }
        return tempNumbers;
    }

    private void collectProducts(List<String> rawProducts) {
        for (int i = 0; i < rawProducts.size(); i++) {
            String rawProduct = removeSquareBrackets(rawProducts.get(i));
            String[] sliceProductInformation = rawProduct.split(",");
            try {
                String productName = sliceProductInformation[0];
                Integer productPrice = Integer.parseInt(sliceProductInformation[1]);
                Integer productQuantity = Integer.parseInt(sliceProductInformation[2]);
                validatePrice(productPrice);
                products.add(new Product(productName, productPrice, productQuantity));
            } catch (IllegalArgumentException e) {
                setProducts();
            }
        }
    }

    private String removeSquareBrackets(String rawProduct) {
        return rawProduct.substring(1, rawProduct.length() - 1);
    }

    private void validatePrice(Integer productPrice) {
        if (productPrice < 100) {
            System.out.println("[ERROR] 상품 가격은 100원 이상이어야 합니다.");
            throw new IllegalArgumentException();
        }
        if ((productPrice % 10) != 0) {
            System.out.println("[ERROR] 상품 가격은 10으로 나누어 떨어져야 합니다.");
            throw new IllegalArgumentException();
        }
    }

    private boolean purchasePossible(int insertedMoney) {
        if (minPrice() > insertedMoney) {
            return false;
        }
        if (allSoldOut()) {
            return false;
        }
        return true;
    }

    private int minPrice() {
        List<Integer> integerStream = products.stream().map(Product::getPrice).collect(Collectors.toList());
        return Collections.min(integerStream);
    }

    private boolean allSoldOut() {
        return products.stream().filter(Product::soldOut).count() == products.size();
    }

    private void changeMoney(int insertedMoney) {
        Set<Coin> coins = coinBox.keySet();
        for (Coin coin : coins) {
            Integer coinCount = coinBox.get(coin);
            if (coinCount * coin.getCoinValue() > insertedMoney) {
                coinCount = insertedMoney / coin.getCoinValue();
            }
            speaker.announceCoinAmount(coin, coinCount);
            insertedMoney -= coinCount * coin.getCoinValue();
        }
    }

    private int sellProduct(String wantedProduct) {
        Product product = products.stream().filter(p -> p.getName().equals(wantedProduct)).findFirst().orElseThrow(IllegalArgumentException::new);
        product.sell();
        return product.getPrice();
    }
}
