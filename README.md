## 🚀 기능 구현 목록

1. 자판기가 보유할 금액을 입력하고 이 금액에 맞게 동전을 가진다.
    - 자판기가 보유할 금액을 입력한다.
    - 입력받은 금액으로 500, 100, 50, 10 원 금액을 무작위로 생성한다.


2. 상품명, 가격, 수량을 입력받는다.
    - [상품명,가격,수량] 의 형태로 상품에 대한 정보를 입력받는다.
    - [];[] 의 형태로 세미 콜론으로 상품들을 구분한다.
    - 상품의 가격은 100원부터 시작한다.
    - 상품의 가격은 10원으로 나누어 떨어져야 한다.
    - 위의 조건을 맞추지 못하면 IllegalArgumentException 을 발생시키고 다시 입력받는다.


3. 살 수 없을 때까지 상품을 구매한다.
    - 금액을 투입한다.
    - 현재 남은 금액을 알려준다.
    - 구매할 상품을 입력받는다.
    - 해당 상품의 가격만큼 남은 금액에서 뺀다.
    - 아래의 경우에 대해 구매 행위를 멈추고 잔돈을 반환한다.
        * 남은 금액이 상품의 최저 가격보다 적으면 잔돈을 반환한다.
        * 모든 상품이 소진된 경우 잔돈을 반환한다.

4. 사용자가 잘못된 값을 입력할 경우 IllegalArgumentException 을 발생시킨다.

## 🛠 리팩토링 목록

- 비대해진 VendingMachine 의 기능들을 분리
   * 비즈니스 로직과 UI 로직 분리
   * 동전 랜덤 생성 알고리즘 수정
      * 변경 전: 동전 최대 생성 갯수 리스트를 pickNumberList()의 파라미터로 전달
        - 예: 자판기 보유 금액이 450원이고 100원 동전을 무작위로 생성할 때 파라미터로 (0, 1, 2, 3, 4)를 전달
      * 변경 후: 동전 amount 리스트를 pickNumberList()의 파라미터로 전달
   * 메소드 세분화
   * 예외 처리
      - 금액에 숫자가 아닌 문자가 들어가면 예외 처리
      - 금액이 10으로 나누어 떨어지지 않으면 예외 처리
      - 상품 등록 형식에 맞지 않으면 예외 처리
      - 상품을 등록하지 않으면 예외처리
      - 상품이 중복되면 예외처리
      - 상품 가격의 기준(100원 이상, 10의 배수)을 충족하지 못하면 예외 처리
      - 진열된 상품이 아닌 상품을 구매 요청하면 예외 처리
   * 매직 넘버 상수화
   * 메소드 순서 맞추기
   