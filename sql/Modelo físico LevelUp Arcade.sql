create database levelup_arcade;
use levelup_arcade;

create table usuarios (
	id_usuario int primary key auto_increment,
	nombre_usuario varchar(50) unique not null,
    nombre varchar(100) not null,
    contrasena_hash varchar(255) not null,
    rol enum('administrador','empleado') not null
);

create table categorias (
	id_categoria int primary key auto_increment,
    nombre varchar(100) not null,
    descripcion varchar(500)
);

create table proveedores (
	id_proveedor int primary key auto_increment,
    nombre varchar(100) not null,
    email varchar(100) unique not null,
    direccion varchar(100),
    telefono varchar(20)
);

create table clientes (
	id_cliente int primary key auto_increment,
    nombre varchar(100) not null,
    email varchar (100) unique not null,
    telefono varchar (20),
    direccion varchar(100)
);

create table productos (
	id_producto int primary key auto_increment,
    id_categoria int,
    id_proveedor int,
    nombre varchar(100) not null,
    anio_lanzamiento year,
    plataforma varchar(50),
    precio decimal(10,2) not null,
    stock int default 0 not null,
    descripcion text,
	foreign key (id_categoria) references categorias(id_categoria) on delete restrict,
    foreign key (id_proveedor) references proveedores(id_proveedor) on delete restrict
);

create table pedidos (
	id_pedido int primary key auto_increment,
    id_cliente int not null,
    fecha datetime default current_timestamp not null,
    estado varchar(20) default 'pendiente',
	foreign key (id_cliente) references clientes(id_cliente) on delete restrict
);

create table linea_pedido (
    id_pedido int not null,
    id_producto int not null,
    cantidad int not null,
    precio_unitario decimal(10,2) not null,
    primary key(id_pedido, id_producto),
    foreign key (id_pedido) references pedidos(id_pedido) on delete cascade,
    foreign key (id_producto) references productos(id_producto) on delete restrict
);


