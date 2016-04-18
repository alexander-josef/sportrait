
    create table Customers (
        customerId int8 not null,
        firstName varchar(255) not null,
        lastName varchar(255) not null,
        title varchar(255),
        addr1 varchar(255) not null,
        addr2 varchar(255),
        zipCode varchar(255) not null,
        city varchar(255) not null,
        state varchar(255),
        country varchar(255) not null,
        gender varchar(1) not null,
        oipsUsername varchar(255),
        oipsPassword varchar(255),
        email varchar(255),
        phone varchar(255),
        fax varchar(255),
        rememberMe bool,
        noEmailFromCopla bool,
        noUnartigAccount bool,
        primary key (customerId)
    );

    create table EventCategories (
        eventCategoryId int8 not null,
        title varchar(255) not null,
        description varchar(255),
        eventId int8,
        category_position int4,
        primary key (eventCategoryId)
    );

    create table EventRunners (
        eventId int8 not null,
        photoSubjectId int8 not null,
        startnumber varchar(255),
        rank int4,
        runningTime varchar(255),
        primary key (eventId, photoSubjectId)
    );

    create table GenericLevels (
        genericLevelId int8 not null,
        HIERARCHY_LEVEL varchar(255) not null,
        navTitle varchar(30),
        longTitle varchar(255),
        description text,
        quickAccess varchar(255),
        isPrivate bool,
        publish bool,
        privateAccessCode varchar(255),
        categoryId int8,
        zipcode varchar(255),
        city varchar(255),
        eventDate date,
        weblink varchar(255),
        eventGroupId int8,
        albumTypeString varchar(255),
        photographerId int8,
        eventId int8,
        eventCategoryId int8,
        primary key (genericLevelId)
    );

    create table OrderHashes (
        orderHashId int8 not null,
        hash varchar(255) not null,
        expiryDate timestamp,
        orderId int8,
        primary key (orderHashId)
    );

    create table OrderItems (
        orderItemId int8 not null,
        quantity int4 not null,
        photoFileName varchar(255),
        photoId int8,
        productId int8,
        orderId int8,
        primary key (orderItemId)
    );

    create table Orders (
        orderId int8 not null,
        orderDate timestamp not null,
        uploadCompletedDate timestamp,
        oipsOrderId varchar(255),
        customerId int8,
        primary key (orderId)
    );

    create table PhotoSubjects (
        photoSubjectId int8 not null,
        name varchar(255),
        age int4,
        primary key (photoSubjectId)
    );

    create table PhotoSubjects2Photos (
        photoId int8 not null,
        photoSubjectId int8 not null,
        primary key (photoSubjectId, photoId)
    );

    create table Photographers (
        photographerId int8 not null,
        cameraModel varchar(255),
        website varchar(255),
        contactInformation text,
        primary key (photographerId)
    );

    create table Photos (
        photoId int8 not null,
        filename varchar(255) not null,
        displayTitle varchar(255) not null,
        widthPixels int4 not null,
        heightPixels int4 not null,
        pictureTakenDate timestamp,
        uploadDate timestamp,
        albumId int8,
        primary key (photoId)
    );

    create table Prices (
        priceId int8 not null,
        priceCHF numeric(19, 2) not null,
        priceEUR numeric(19, 2) not null,
        priceGBP numeric(19, 2) not null,
        priceSEK numeric(19, 2) not null,
        comment varchar(255) not null,
        primary key (priceId)
    );

    create table Prices2ProductTypes (
        productTypeId int8 not null,
        priceId int8 not null,
        primary key (productTypeId, priceId)
    );

    create table ProductTypes (
        productTypeId int8 not null,
        name varchar(255) not null,
        description varchar(255) not null,
        digitalProduct bool not null,
        primary key (productTypeId)
    );

    -- Caution! parameter "inactive" is missing ... better generate again from scratch

    create table Products (
        productId int8 not null,
        productName varchar(255),
        priceId int8,
        productTypeId int8,
        albumId int8,
        primary key (productId)
    );

    create table UserProfiles (
        userProfileId int8 not null,
        userName varchar(255) not null unique,
        password varchar(255) not null,
        firstName varchar(255) not null,
        lastName varchar(255) not null,
        emailAddress varchar(255) not null,
        phone varchar(255) not null,
        phoneMobile varchar(255) not null,
        title varchar(255),
        addr1 varchar(255) not null,
        addr2 varchar(255),
        zipCode varchar(255) not null,
        city varchar(255) not null,
        state varchar(255),
        country varchar(255) not null,
        gender varchar(1) not null,
        primary key (userProfileId)
    );

    create table UserProfiles2UserRoles (
        userName varchar(255) not null,
        roleName varchar(255) not null,
        primary key (userName, roleName)
    );

    create table UserRoles (
        userRoleId int8 not null,
        roleName varchar(255),
        roleDescription varchar(255),
        primary key (userRoleId)
    );

    create index EventCategory_Event_INDEX on EventCategories (eventId);

    alter table EventCategories 
        add constraint FKF81F5436CC92EA5 
        foreign key (eventId) 
        references GenericLevels;

    alter table EventRunners 
        add constraint FK9C5D3F69DDBF42A4 
        foreign key (eventId) 
        references GenericLevels;

    alter table EventRunners 
        add constraint FK9C5D3F6911E71B7A 
        foreign key (photoSubjectId) 
        references PhotoSubjects;

    create index GenericLevel_Event_INDEX on GenericLevels (eventId);

    create index GenericLevel_EventGroup_INDEX on GenericLevels (eventGroupId);

    create index GenericLevel_EventCategory_INDEX on GenericLevels (eventCategoryId);

    create index GenericLevel_Category_INDEX on GenericLevels (categoryId);

    create index GenericLevel_Photographer_INDEX on GenericLevels (photographerId);

    alter table GenericLevels 
        add constraint FK28FFFE06F8B3C4A2 
        foreign key (categoryId) 
        references GenericLevels;

    alter table GenericLevels 
        add constraint FK28FFFE06867E4040 
        foreign key (eventCategoryId) 
        references EventCategories;

    alter table GenericLevels 
        add constraint FK28FFFE0651DD3E50 
        foreign key (eventGroupId) 
        references GenericLevels;

    alter table GenericLevels 
        add constraint FK28FFFE06DDBF42A4 
        foreign key (eventId) 
        references GenericLevels;

    alter table GenericLevels 
        add constraint FK28FFFE06D9632678 
        foreign key (photographerId) 
        references Photographers;

    create index OrderHash_Hash_INDEX on OrderHashes (hash);

    create index OrderHash_Order_INDEX on OrderHashes (orderId);

    alter table OrderHashes 
        add constraint FKB0C9AB0AE8630F0C 
        foreign key (orderId) 
        references Orders;

    create index OrderItem_Order_INDEX on OrderItems (orderId);

    create index OrderItem_Photo_INDEX on OrderItems (photoId);

    create index OrderItem_Product_INDEX on OrderItems (productId);

    alter table OrderItems 
        add constraint FKA2B1AD32E8630F0C 
        foreign key (orderId) 
        references Orders;

    alter table OrderItems 
        add constraint FKA2B1AD3269F854CE 
        foreign key (productId) 
        references Products;

    alter table OrderItems 
        add constraint FKA2B1AD32CE453D4 
        foreign key (photoId) 
        references Photos;

    create index Order_Customer_INDEX on Orders (customerId);

    alter table Orders 
        add constraint FK8D444F056E4C1262 
        foreign key (customerId) 
        references Customers;

    alter table PhotoSubjects2Photos 
        add constraint FK53AEDC3ACE453D4 
        foreign key (photoId) 
        references Photos;

    alter table PhotoSubjects2Photos 
        add constraint FK53AEDC3A11E71B7A 
        foreign key (photoSubjectId) 
        references PhotoSubjects;

    alter table Photographers 
        add constraint FK393AB96A54D76D57 
        foreign key (photographerId) 
        references UserProfiles;

    create index Photo_Album_INDEX on Photos (albumId);

    alter table Photos 
        add constraint FK8E7174A1F8B1B90E 
        foreign key (albumId) 
        references GenericLevels;

    alter table Prices2ProductTypes 
        add constraint FKE4BF89F21D9CD042 
        foreign key (priceId) 
        references Prices;

    alter table Prices2ProductTypes 
        add constraint FKE4BF89F26D4EEF62 
        foreign key (productTypeId) 
        references ProductTypes;

    create index Product_ProductPrice_INDEX on Products (priceId);

    create index Product_ProductType_INDEX on Products (productTypeId);

    alter table Products 
        add constraint FKC80635841D9CD042 
        foreign key (priceId) 
        references Prices;

    alter table Products 
        add constraint FKC8063584F8B1B90E 
        foreign key (albumId) 
        references GenericLevels;

    alter table Products 
        add constraint FKC80635846D4EEF62 
        foreign key (productTypeId) 
        references ProductTypes;

    create sequence sequence_CustomerId;

    create sequence sequence_OrderId;

    create sequence sequence_OrderitemId;

    create sequence sequence_UserProfileId;

    create sequence sequence_eventCategoryId;

    create sequence sequence_genericLevelId;

    create sequence sequence_orderHashId;

    create sequence sequence_photoId;

    create sequence sequence_photoSubjectId;

    create sequence sequence_priceId;

    create sequence sequence_productId;

    create sequence sequence_productTypeId;

    create sequence sequence_userRoleId;
