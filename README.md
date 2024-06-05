# SpringBatch QuerydslItemReader

## Usage

- Java 17
- SpringBoot 3.2.5
- SpringBatch 5.1.1
- Querydsl 5.0.0

## Dependency

```
repositories {
	maven { url 'https://jitpack.io' }
}

dependencies {
	implementation 'com.github.ywj9811:querydsl-itemreader:v1.0.1'
}
```
### QueryDslPagingItemReader
```java
@Bean
public QueryDslPagingItemReader<Product> reader() {
    return new QueryDslPagingItemReader<>(emf, chunkSize, queryFactory -> queryFactory
            .selectFrom(product)
            .where(product.createDate.eq(txDate)));
}
```

### QueryDslZeroPagingItemReader (offset is always zero)
```java
public QueryDslZeroPagingItemReader<Product> reader() {
    return new QueryDslZeroPagingItemReader<>(emf, chunkSize, queryFactory -> queryFactory
            .selectFrom(product)
            .where(product.createDate.eq(txDate)));
```

### QueryDslNoOffsetPagingItemReader
```java
@Bean
public QueryDslNoOffsetPagingItemReader<Product> reader() {
    // 1. No Offset Option with Number
    QueryDslNoOffsetNumberOptions<Product, Long> options =
            new QueryDslNoOffsetNumberOptions<>(product.id, Expression.ASC);

    // 2. QueryDsl Reader
    return new QueryDslNoOffsetPagingItemReader<>(emf, chunkSize, options, queryFactory -> queryFactory
                    .selectFrom(product)
                    .where(product.createDate.eq(txDate)));
}
```
```java
public QueryDslNoOffsetPagingItemReader<Product> reader() {
    // 1. No Offset Option with String
    QueryDslNoOffsetStringOptions<Product> options =
            new QueryDslNoOffsetStringOptions<>(product.name, Expression.DESC);

    // 2. QueryDsl Reader
    return new QueryDslNoOffsetPagingItemReader<>(emf, chunkSize, options, queryFactory -> queryFactory
            .selectFrom(product)
            .where(product.createDate.eq(txDate)));
}
```

### 참조
  - https://github.com/jojoldu/spring-batch-querydsl
  - https://jojoldu.tistory.com/473
    
  인프런 CTO **이동욱님** 게시글과 코드의 굉장히 많은 부분은 참고하였습니다.<br>
  여러가지 버전이 달라 오류가 생기는 부분을 변경하여 사용하기 위한 라이브러리 입니다.
