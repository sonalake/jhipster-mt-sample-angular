package com.companyslack.app.web.rest;

import com.companyslack.app.domain.Channels;
import com.companyslack.app.service.ChannelsService;
import com.companyslack.app.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.companyslack.app.domain.Channels}.
 */
@RestController
@RequestMapping("/api")
public class ChannelsResource {

    private final Logger log = LoggerFactory.getLogger(ChannelsResource.class);

    private static final String ENTITY_NAME = "channels";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChannelsService channelsService;

    public ChannelsResource(ChannelsService channelsService) {
        this.channelsService = channelsService;
    }

    /**
     * {@code POST  /channels} : Create a new channels.
     *
     * @param channels the channels to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new channels, or with status {@code 400 (Bad Request)} if the channels has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/channels")
    public ResponseEntity<Channels> createChannels(@Valid @RequestBody Channels channels) throws URISyntaxException {
        log.debug("REST request to save Channels : {}", channels);
        if (channels.getId() != null) {
            throw new BadRequestAlertException("A new channels cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Channels result = channelsService.save(channels);
        return ResponseEntity.created(new URI("/api/channels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /channels} : Updates an existing channels.
     *
     * @param channels the channels to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated channels,
     * or with status {@code 400 (Bad Request)} if the channels is not valid,
     * or with status {@code 500 (Internal Server Error)} if the channels couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/channels")
    public ResponseEntity<Channels> updateChannels(@Valid @RequestBody Channels channels) throws URISyntaxException {
        log.debug("REST request to update Channels : {}", channels);
        if (channels.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Channels result = channelsService.save(channels);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, channels.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /channels} : get all the channels.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of channels in body.
     */
    @GetMapping("/channels")
    public List<Channels> getAllChannels(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Channels");
        return channelsService.findAll();
    }

    /**
     * {@code GET  /channels/:id} : get the "id" channels.
     *
     * @param id the id of the channels to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the channels, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/channels/{id}")
    public ResponseEntity<Channels> getChannels(@PathVariable Long id) {
        log.debug("REST request to get Channels : {}", id);
        Optional<Channels> channels = channelsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(channels);
    }

    /**
     * {@code DELETE  /channels/:id} : delete the "id" channels.
     *
     * @param id the id of the channels to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/channels/{id}")
    public ResponseEntity<Void> deleteChannels(@PathVariable Long id) {
        log.debug("REST request to delete Channels : {}", id);
        channelsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
