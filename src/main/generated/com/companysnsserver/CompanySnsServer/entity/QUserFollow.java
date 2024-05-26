package com.companysnsserver.CompanySnsServer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserFollow is a Querydsl query type for UserFollow
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserFollow extends EntityPathBase<UserFollow> {

    private static final long serialVersionUID = -653762406L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserFollow userFollow = new QUserFollow("userFollow");

    public final QUser followUser;

    public final QUserFollowId id;

    public final QUser user;

    public QUserFollow(String variable) {
        this(UserFollow.class, forVariable(variable), INITS);
    }

    public QUserFollow(Path<? extends UserFollow> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserFollow(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserFollow(PathMetadata metadata, PathInits inits) {
        this(UserFollow.class, metadata, inits);
    }

    public QUserFollow(Class<? extends UserFollow> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.followUser = inits.isInitialized("followUser") ? new QUser(forProperty("followUser")) : null;
        this.id = inits.isInitialized("id") ? new QUserFollowId(forProperty("id")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

