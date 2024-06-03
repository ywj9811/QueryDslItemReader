# SpringBatch QuerydslItemReader

## Usage

- Java 17
- SpringBoot 3.xx
- SpringBatch 5.xx
- Querydsl 5.xx

## Dependency

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
		implementation 'com.github.ywj9811:QueryDslItemReader:v0.0.3'
}
```
### QueryDslPagingItemReader
```java
@Bean
public QuerydslPagingItemReader<Product> reader() {
    return new QuerydslPagingItemReader<>(emf, chunkSize, queryFactory -> queryFactory
            .selectFrom(product)
            .where(product.createDate.eq(jobParameter.getTxDate())));
}
```

### QueryDslNoOffsetPagingItemReader
```java
@Bean
public QuerydslNoOffsetPagingItemReader<Product> reader() {
    // 1. No Offset Option with Number
    QuerydslNoOffsetNumberOptions<Product, Long> options =
            new QuerydslNoOffsetNumberOptions<>(product.id, Expression.ASC);

    // 2. Querydsl Reader
    return new QuerydslNoOffsetPagingItemReader<>(emf, chunkSize, options, queryFactory -> queryFactory
                    .selectFrom(product)
                    .where(product.createDate.eq(txDate)));
}
```
```java
public QuerydslNoOffsetPagingItemReader<Product> reader() {
    // 1. No Offset Option with String
    QuerydslNoOffsetStringOptions<Product> options =
            new QuerydslNoOffsetStringOptions<>(product.name, Expression.DESC);

    // 2. Querydsl Reader
    return new QuerydslNoOffsetPagingItemReader<>(emf, chunkSize, options, queryFactory -> queryFactory
            .selectFrom(product)
            .where(product.createDate.eq(txDate)));
}
```

##Logging
```yaml
logging:
  level:
    org:
      springframework:
        batch: 
            item.querydsl.reader: DEBUG
```

### 참조
  - https://github.com/jojoldu/spring-batch-querydsl
  - https://jojoldu.tistory.com/473
    
  인프런 CTO **이동욱님** 게시글과 코드의 굉장히 많은 부분은 참고하였습니다.<br>
  여러가지 버전이 달라 오류가 생기는 부분을 변경하여 사용하기 위한 라이브러리 입니다.
