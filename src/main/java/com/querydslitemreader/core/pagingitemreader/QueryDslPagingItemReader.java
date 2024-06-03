package com.querydslitemreader.core.pagingitemreader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class QueryDslPagingItemReader<T> extends AbstractPagingItemReader<T> {
    protected final Map<String, Object> jpaPropertyMap = new HashMap<>();
    protected EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;
    protected Function<JPAQueryFactory, JPAQuery<T>> queryFunction;

    private boolean transacted = true;// default value

    public QueryDslPagingItemReader() {
        setName(ClassUtils.getShortName(QueryDslPagingItemReader.class));
    }

    public QueryDslPagingItemReader(EntityManagerFactory entityManagerFactory, int pageSize,
                                    Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this();
        this.entityManagerFactory = entityManagerFactory;
        this.queryFunction = queryFunction;
        setPageSize(pageSize);
    }

    /**
     * Create a query using an appropriate query provider (entityManager OR
     * queryProvider).
     */
    /**
     * 이 부분이 private이기 때문에 새롭게 작성하게 된 것! override할 수 없어서
     */
//    private Query createQuery() {
//        if (queryProvider == null) {
//            return entityManager.createQuery(queryString);
//        }
//        else {
//            return queryProvider.createQuery();
//        }
//    }
    /**
     * By default (true) the EntityTransaction will be started and committed around the
     * read. Can be overridden (false) in cases where the JPA implementation doesn't
     * support a particular transaction. (e.g. Hibernate with a JTA transaction). NOTE:
     * may cause problems in guaranteeing the object consistency in the
     * EntityManagerFactory.
     * @param transacted indicator
     */
    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    @Override
    protected void doOpen() throws Exception {
        super.doOpen();

        entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap);
        if (entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doReadPage() {
        clearIfTransacted();

        JPQLQuery<T> query = createQuery()
                .offset(getPage() * getPageSize())
                .limit(getPageSize());

        initResults();

        fetchQuery(query);
    }

    protected void clearIfTransacted() {
        if (transacted) {
            entityManager.clear();
        }
    }

    protected JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        return queryFunction.apply(queryFactory);
    }

    protected void initResults() {
        if (CollectionUtils.isEmpty(results)) {
            results = new ArrayList<>();
        } else {
            results.clear();
        }
    }

    protected void fetchQuery(JPQLQuery<T> query) {
        if (!transacted) {
            List<T> queryResult = query.fetch();
            for (T entity : queryResult) {
                entityManager.detach(entity);
                results.add(entity);
            }
        } else {
            results.addAll(query.fetch());
        }
    }

    @Override
    protected void doClose() throws Exception {
        entityManager.close();
        super.doClose();
    }
}
