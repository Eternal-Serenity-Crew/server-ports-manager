package org.esc.serverportsmanager.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.esc.serverportsmanager.io.converters.ConvertableToHttpResponse

@Entity
@Table(name = "ports_storage")
data class PortsStorage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, unique = true)
    var portNumber: Long,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: Users,
) : ConvertableToHttpResponse<PortsStorage>