package com.companyslack.app.service;

import com.companyslack.app.domain.Channels;
import com.companyslack.app.repository.ChannelsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Channels}.
 */
@Service
@Transactional
public class ChannelsService {

    private final Logger log = LoggerFactory.getLogger(ChannelsService.class);

    private final ChannelsRepository channelsRepository;

    public ChannelsService(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    /**
     * Save a channels.
     *
     * @param channels the entity to save.
     * @return the persisted entity.
     */
    public Channels save(Channels channels) {
        log.debug("Request to save Channels : {}", channels);
        return channelsRepository.save(channels);
    }

    /**
     * Get all the channels.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Channels> findAll() {
        log.debug("Request to get all Channels");
        return channelsRepository.findAllWithEagerRelationships();
    }

    /**
     * Get all the channels with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Channels> findAllWithEagerRelationships(Pageable pageable) {
        return channelsRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one channels by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Channels> findOne(Long id) {
        log.debug("Request to get Channels : {}", id);
        return channelsRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the channels by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Channels : {}", id);
        channelsRepository.deleteById(id);
    }
}
