create table usuarios (
	id_usuario int primary key auto_increment,
    nombre varchar(100),
    contrasena_hash varchar(255),
    rol enum('administrador','empleado')
);

create table categorias (
	id_categoria int primary key auto_increment,
    nombre varchar(100),
    descripcion varchar(200)
);

create table proveedores (
	id_proveedor int primary key auto_increment,
    nombre varchar(100),
    direccion varchar(100),
    telefono varchar(20)
);

create table clientes (
	id_cliente int primary key auto_increment,
    nombre varchar(100),
    email varchar (100) unique,
    telefono varchar (20),
    direccion varchar(100)
);

create table productos (
	id_producto int primary key auto_increment,
    id_categoria int,
    id_proveedor int,
    nombre varchar(100),
    anio_lanzamiento year,
    plataforma varchar(50),
    precio decimal(10,2),
    stock int,
    descripcion varchar(200),
	foreign key (id_categoria) references categorias(id_categoria),
    foreign key (id_proveedor) references proveedores(id_proveedor)
);

create table pedidos (
	id_pedidos int primary key auto_increment,
    id_cliente int,
    id_producto int,
    cantidad int,
    precio_unitario decimal(10,2),
    fecha date,
	foreign key (id_cliente) references clientes(id_cliente),
	foreign key (id_producto) references productos(id_producto)
);



