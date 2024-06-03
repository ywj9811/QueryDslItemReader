package com.querydslitemreader.core.pagingitemreader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.util.ClassUtils;

import java.util.function.Function;

public class QueryDslZeroPagingItemReader<T> extends QueryDslPagingItemReader<T> {

    public QueryDslZeroPagingItemReader() {
        super();
        setName(ClassUtils.getShortName(QueryDslZeroPagingItemReader.class));
    }

    public QueryDslZeroPagingItemReader(EntityManagerFactory entityManagerFactory,
                                        int pageSize,
                                        Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this();
        setTransacted(true);
        super.entityManagerFactory = entityManagerFactory;
        super.queryFunction = queryFunction;
        setPageSize(pageSize);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doReadPage() {

        JPQLQuery<T> query = createQuery()
                .offset(0)
                .limit(getPageSize());

        initResults();

        fetchQuery(query);
    }
}
