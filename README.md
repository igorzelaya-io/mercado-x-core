# MercadoX Core Service

## Overview

`mercado-x-core` is the central business domain service of the MercadoX ecosystem.

It handles core domain logic and orchestrates communication with other microservices via Kafka.

---

## Responsibilities

- Core business logic
- Domain orchestration
- Publishing Kafka events
- Delegating async operations (email, WhatsApp, etc.)
- Managing Orders, Leads, Inventory, etc.

---

## Architecture

- Event-driven architecture
- Kafka-based communication
- Multi-tenant aware
- Clean Domain separation

---

## Dependencies

- mercado-x-library-entity
- mercado-x-library-jpa
- mercado-x-context

---

## Does NOT Handle

- Authentication (delegated to OAuth)
- Direct third-party integrations (delegated to Email service)