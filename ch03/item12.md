# toString을 항상 재정의하라

- 디버깅이 쉬워짐.
- 주요정보를 모두 반환하는게 좋다.
- 하위 클래스의 toString을 모두 재정의 하라.
- 특정 포맷에 따라 정의도 가능. ex. csv
- 명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리 메서드를 제공하면 좋다. ex. BigInteger
- 포맷 명시 여부보단, 의도를 명확하게 밝히는 것이 중요하다.