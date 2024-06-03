package com.querydslitemreader.core.pagingitemreader.options;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydslitemreader.core.pagingitemreader.expression.Expression;
import jakarta.annotation.Nonnull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryDslNoOffsetStringOptions<T> extends QueryDslNoOffsetOptions<T> {

    private String currentId;
    private String lastId;

    private final StringPath field;

    public QueryDslNoOffsetStringOptions(@Nonnull StringPath field, @NonNull Expression expression) {
        super(field, expression);
        this.field = field;
    }

    public String getCurrentId() {
        return currentId;
    }

    public String getLastId() {
        return lastId;
    }

    @Override
    public void initKeys(JPAQuery<T> query, int page) {
        if(page == 0) {
            initFirstId(query);
            initLastId(query);

            log.debug("First Ke y= {}, Last Key = {}", currentId, lastId);
        }
    }

    @Override
    protected void initFirstId(JPAQuery<T> query) {
        JPAQuery<T> clone = query.clone();
        boolean isGroupByQuery = isGroupByQuery(clone);

        if(isGroupByQuery) {
            currentId = clone
                    .select(field)
                    .orderBy(expression.isAsc()? field.asc() : field.desc())
                    .fetchFirst();
        } else {
            currentId = clone
                    .select(expression.isAsc()? field.min(): field.max())
                    .fetchFirst();
        }

    }

    @Override
    protected void initLastId(JPAQuery<T> query) {
        JPAQuery<T> clone = query.clone();
        boolean isGroupByQuery = isGroupByQuery(clone);

        if(isGroupByQuery) {
            lastId = clone
                    .select(field)
                    .orderBy(expression.isAsc()? field.desc() : field.asc())
                    .fetchFirst();
        } else {
            lastId = clone
                    .select(expression.isAsc()? field.max(): field.min())
                    .fetchFirst();
        }
    }

    @Override
    public JPAQuery<T> createQuery(JPAQuery<T> query, int page) {
        if (currentId == null) {
            return query;
        }

        return query
                .where(whereExpression(page))
                .orderBy(orderExpression());
    }

    private BooleanExpression whereExpression(int page) {
        return expression.where(field, page, currentId)
                .and(expression.isAsc()? field.loe(lastId) : field.goe(lastId));
    }

    private OrderSpecifier<String> orderExpression() {
        return expression.order(field);
    }

    @Override
    public void resetCurrentId(T item) {
        currentId = (String) getFiledValue(item);
        log.debug("Current Select Key = {}", currentId);
    }
}
