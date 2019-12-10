package com.companyslack.app.web.rest;

import com.companyslack.app.SampleMultitenancyAppAngularApp;
import com.companyslack.app.domain.Channels;
import com.companyslack.app.domain.Company;
import com.companyslack.app.repository.ChannelsRepository;
import com.companyslack.app.service.ChannelsService;
import com.companyslack.app.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.companyslack.app.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ChannelsResource} REST controller.
 */
@SpringBootTest(classes = SampleMultitenancyAppAngularApp.class)
public class ChannelsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ChannelsRepository channelsRepository;

    @Mock
    private ChannelsRepository channelsRepositoryMock;

    @Mock
    private ChannelsService channelsServiceMock;

    @Autowired
    private ChannelsService channelsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restChannelsMockMvc;

    private Channels channels;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChannelsResource channelsResource = new ChannelsResource(channelsService);
        this.restChannelsMockMvc = MockMvcBuilders.standaloneSetup(channelsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Channels createEntity(EntityManager em) {
        Channels channels = new Channels()
            .name(DEFAULT_NAME);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        channels.setCompany(company);
        return channels;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Channels createUpdatedEntity(EntityManager em) {
        Channels channels = new Channels()
            .name(UPDATED_NAME);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createUpdatedEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        channels.setCompany(company);
        return channels;
    }

    @BeforeEach
    public void initTest() {
        channels = createEntity(em);
    }

    @Test
    @Transactional
    public void createChannels() throws Exception {
        int databaseSizeBeforeCreate = channelsRepository.findAll().size();

        // Create the Channels
        restChannelsMockMvc.perform(post("/api/channels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(channels)))
            .andExpect(status().isCreated());

        // Validate the Channels in the database
        List<Channels> channelsList = channelsRepository.findAll();
        assertThat(channelsList).hasSize(databaseSizeBeforeCreate + 1);
        Channels testChannels = channelsList.get(channelsList.size() - 1);
        assertThat(testChannels.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createChannelsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = channelsRepository.findAll().size();

        // Create the Channels with an existing ID
        channels.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChannelsMockMvc.perform(post("/api/channels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(channels)))
            .andExpect(status().isBadRequest());

        // Validate the Channels in the database
        List<Channels> channelsList = channelsRepository.findAll();
        assertThat(channelsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllChannels() throws Exception {
        // Initialize the database
        channelsRepository.saveAndFlush(channels);

        // Get all the channelsList
        restChannelsMockMvc.perform(get("/api/channels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(channels.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllChannelsWithEagerRelationshipsIsEnabled() throws Exception {
        ChannelsResource channelsResource = new ChannelsResource(channelsServiceMock);
        when(channelsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restChannelsMockMvc = MockMvcBuilders.standaloneSetup(channelsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restChannelsMockMvc.perform(get("/api/channels?eagerload=true"))
        .andExpect(status().isOk());

        verify(channelsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllChannelsWithEagerRelationshipsIsNotEnabled() throws Exception {
        ChannelsResource channelsResource = new ChannelsResource(channelsServiceMock);
            when(channelsServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restChannelsMockMvc = MockMvcBuilders.standaloneSetup(channelsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restChannelsMockMvc.perform(get("/api/channels?eagerload=true"))
        .andExpect(status().isOk());

            verify(channelsServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getChannels() throws Exception {
        // Initialize the database
        channelsRepository.saveAndFlush(channels);

        // Get the channels
        restChannelsMockMvc.perform(get("/api/channels/{id}", channels.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(channels.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingChannels() throws Exception {
        // Get the channels
        restChannelsMockMvc.perform(get("/api/channels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChannels() throws Exception {
        // Initialize the database
        channelsService.save(channels);

        int databaseSizeBeforeUpdate = channelsRepository.findAll().size();

        // Update the channels
        Channels updatedChannels = channelsRepository.findById(channels.getId()).get();
        // Disconnect from session so that the updates on updatedChannels are not directly saved in db
        em.detach(updatedChannels);
        updatedChannels
            .name(UPDATED_NAME);

        restChannelsMockMvc.perform(put("/api/channels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChannels)))
            .andExpect(status().isOk());

        // Validate the Channels in the database
        List<Channels> channelsList = channelsRepository.findAll();
        assertThat(channelsList).hasSize(databaseSizeBeforeUpdate);
        Channels testChannels = channelsList.get(channelsList.size() - 1);
        assertThat(testChannels.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingChannels() throws Exception {
        int databaseSizeBeforeUpdate = channelsRepository.findAll().size();

        // Create the Channels

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChannelsMockMvc.perform(put("/api/channels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(channels)))
            .andExpect(status().isBadRequest());

        // Validate the Channels in the database
        List<Channels> channelsList = channelsRepository.findAll();
        assertThat(channelsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteChannels() throws Exception {
        // Initialize the database
        channelsService.save(channels);

        int databaseSizeBeforeDelete = channelsRepository.findAll().size();

        // Delete the channels
        restChannelsMockMvc.perform(delete("/api/channels/{id}", channels.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Channels> channelsList = channelsRepository.findAll();
        assertThat(channelsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Channels.class);
        Channels channels1 = new Channels();
        channels1.setId(1L);
        Channels channels2 = new Channels();
        channels2.setId(channels1.getId());
        assertThat(channels1).isEqualTo(channels2);
        channels2.setId(2L);
        assertThat(channels1).isNotEqualTo(channels2);
        channels1.setId(null);
        assertThat(channels1).isNotEqualTo(channels2);
    }
}
