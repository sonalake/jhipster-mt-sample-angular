package com.companyslack.app.repository;
import com.companyslack.app.domain.Channels;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Channels entity.
 */
@Repository
public interface ChannelsRepository extends JpaRepository<Channels, Long> {

    @Query(value = "select distinct channels from Channels channels left join fetch channels.users",
        countQuery = "select count(distinct channels) from Channels channels")
    Page<Channels> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct channels from Channels channels left join fetch channels.users")
    List<Channels> findAllWithEagerRelationships();

    @Query("select channels from Channels channels left join fetch channels.users where channels.id =:id")
    Optional<Channels> findOneWithEagerRelationships(@Param("id") Long id);

}
