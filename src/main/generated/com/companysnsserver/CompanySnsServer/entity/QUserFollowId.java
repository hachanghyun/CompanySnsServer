package com.companysnsserver.CompanySnsServer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserFollowId is a Querydsl query type for UserFollowId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserFollowId extends BeanPath<UserFollowId> {

    private static final long serialVersionUID = -1200444587L;

    public static final QUserFollowId userFollowId = new QUserFollowId("userFollowId");

    public final NumberPath<Long> followUserId = createNumber("followUserId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserFollowId(String variable) {
        super(UserFollowId.class, forVariable(variable));
    }

    public QUserFollowId(Path<? extends UserFollowId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserFollowId(PathMetadata metadata) {
        super(UserFollowId.class, metadata);
    }

}

