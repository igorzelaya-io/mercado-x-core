# Graph Report - .  (2026-08-21)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 406 nodes · 829 edges · 22 communities (21 shown, 1 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 49 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `87bd0ce8`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- OrderService
- LeadService.java
- ItemService
- LocationService
- OrganizationService
- UserService
- EmailDispatchUtils
- OrderController.java
- DriverController.java
- CartController.java
- MercadoXControllerTest.java
- AbstractControllerTest
- CartService
- CategoryService
- OrderQueryService.java
- MercadoXCoreAuthConfig.java
- CategoryController.java
- ItemController.java
- LeadController.java
- mvnw
- mercado-x-core

## God Nodes (most connected - your core abstractions)
1. `OrderService` - 22 edges
2. `ItemService` - 21 edges
3. `UserService` - 20 edges
4. `EmailDispatchUtils` - 16 edges
5. `OrderController` - 13 edges
6. `LocationService` - 13 edges
7. `OrganizationService` - 13 edges
8. `CartService` - 11 edges
9. `CategoryService` - 11 edges
10. `LeadService` - 11 edges

## Surprising Connections (you probably didn't know these)
- `DriverController` --references--> `UserService`  [EXTRACTED]
  src/main/java/hn/shadowcore/mercadox/core/controller/DriverController.java → src/main/java/hn/shadowcore/mercadox/core/service/UserService.java
- `OrderController` --references--> `OrderMapper`  [EXTRACTED]
  src/main/java/hn/shadowcore/mercadox/core/controller/OrderController.java → src/main/java/hn/shadowcore/mercadox/core/mapper/OrderMapper.java
- `CartService` --references--> `ItemMapper`  [EXTRACTED]
  src/main/java/hn/shadowcore/mercadox/core/service/CartService.java → src/main/java/hn/shadowcore/mercadox/core/mapper/ItemMapper.java
- `ItemService` --references--> `CategoryService`  [EXTRACTED]
  src/main/java/hn/shadowcore/mercadox/core/service/ItemService.java → src/main/java/hn/shadowcore/mercadox/core/service/CategoryService.java
- `OrderService` --references--> `ItemService`  [EXTRACTED]
  src/main/java/hn/shadowcore/mercadox/core/service/OrderService.java → src/main/java/hn/shadowcore/mercadox/core/service/ItemService.java

## Import Cycles
- None detected.

## Communities (22 total, 1 thin omitted)

### Community 0 - "OrderService"
Cohesion: 0.10
Nodes (29): IdempotentOperation, Logger, NotificationTemplateName, OrderItem, OrderItemRepository, OrderUseCase, Component, Order (+21 more)

### Community 1 - "LeadService.java"
Cohesion: 0.10
Nodes (29): ClientLeadUseCase, ExtendWith, Lead, LeadEventPublisher, Mapping, ProducerRecord, Slf4j, LeadCreatedEvent (+21 more)

### Community 2 - "ItemService"
Cohesion: 0.10
Nodes (21): InventoryRepository, ItemUseCase, ItemMapper, Item, ItemDto, Mapper, InventoryService, Inventory (+13 more)

### Community 3 - "LocationService"
Cohesion: 0.14
Nodes (21): LocationRepository, LocationUseCase, GetMapping, LocationDto, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response (+13 more)

### Community 4 - "OrganizationService"
Cohesion: 0.18
Nodes (16): GetMapping, Organization, PostMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response, ResponseEntity (+8 more)

### Community 5 - "UserService"
Cohesion: 0.17
Nodes (13): GetMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response, ResponseEntity, RestController, User (+5 more)

### Community 6 - "EmailDispatchUtils"
Cohesion: 0.18
Nodes (14): EmailEventPublisher, SendResult, Component, OrderEmailEvent, Override, RequiredArgsConstructor, KafkaOrderEventPublisher, EmailDispatchUtils (+6 more)

### Community 7 - "OrderController.java"
Cohesion: 0.23
Nodes (16): DeleteMapping, DispatchOrderRequest, GetMapping, OrderPayload, OrderQueryUseCase, OrderStatus, OrderUseCase, PlaceOrderRequest (+8 more)

### Community 8 - "DriverController.java"
Cohesion: 0.22
Nodes (13): DriverController, GetMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response, ResponseEntity, RestController (+5 more)

### Community 9 - "CartController.java"
Cohesion: 0.29
Nodes (12): CartController, CartDto, CartUseCase, DeleteMapping, GetMapping, PostMapping, PreAuthorize, RequestMapping (+4 more)

### Community 10 - "MercadoXControllerTest.java"
Cohesion: 0.24
Nodes (11): ActiveProfiles, AutoConfigureMockMvc, EnableAsync, EnableCaching, Import, Retention, SpringBootApplication, SpringBootTest (+3 more)

### Community 11 - "AbstractControllerTest"
Cohesion: 0.24
Nodes (10): MockMvc, ObjectMapper, RSAPublicKey, AbstractControllerTest, JwtVerifier, AnonymousTenantValidator, ClientLeadUseCase, OrganizationRepository (+2 more)

### Community 12 - "CartService"
Cohesion: 0.31
Nodes (8): CartRedisRepository, CartUseCase, CartService, CartDto, ItemRepository, Override, RequiredArgsConstructor, Service

### Community 13 - "CategoryService"
Cohesion: 0.29
Nodes (8): CategoryRepository, CategoryUseCase, CategoryService, Category, OrganizationRepository, Override, RequiredArgsConstructor, Service

### Community 14 - "OrderQueryService.java"
Cohesion: 0.29
Nodes (9): OrderQueryUseCase, Cacheable, Order, OrderRepository, OrderStatus, Override, RequiredArgsConstructor, Service (+1 more)

### Community 15 - "MercadoXCoreAuthConfig.java"
Cohesion: 0.30
Nodes (10): Bean, Configuration, EnableMethodSecurity, EnableWebSecurity, HttpSecurity, SecurityFilterChain, AnonymousTenantValidator, JwtVerifier (+2 more)

### Community 16 - "CategoryController.java"
Cohesion: 0.30
Nodes (10): CategoryController, Category, CategoryUseCase, GetMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response (+2 more)

### Community 17 - "ItemController.java"
Cohesion: 0.30
Nodes (10): ItemController, ItemDto, ItemUseCase, PostMapping, PreAuthorize, RequestMapping, RequiredArgsConstructor, Response (+2 more)

### Community 18 - "LeadController.java"
Cohesion: 0.33
Nodes (9): ClientLeadRequest, ClientLeadUseCase, PostMapping, RequestMapping, RequiredArgsConstructor, Response, ResponseEntity, RestController (+1 more)

### Community 19 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

## Knowledge Gaps
- **1 isolated node(s):** `mercado-x-core`
  These have ≤1 connection - possible missing edges or undocumented components.
- **1 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ItemService` connect `ItemService` to `OrderService`, `CategoryService`?**
  _High betweenness centrality (0.200) - this node is a cross-community bridge._
- **Why does `OrderService` connect `OrderService` to `ItemService`, `LocationService`, `OrganizationService`, `UserService`, `EmailDispatchUtils`?**
  _High betweenness centrality (0.196) - this node is a cross-community bridge._
- **What connects `mercado-x-core` to the rest of the system?**
  _1 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `OrderService` be split into smaller, more focused modules?**
  _Cohesion score 0.09898242368177614 - nodes in this community are weakly interconnected._
- **Should `LeadService.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0951219512195122 - nodes in this community are weakly interconnected._
- **Should `ItemService` be split into smaller, more focused modules?**
  _Cohesion score 0.1036036036036036 - nodes in this community are weakly interconnected._
- **Should `LocationService` be split into smaller, more focused modules?**
  _Cohesion score 0.1354679802955665 - nodes in this community are weakly interconnected._