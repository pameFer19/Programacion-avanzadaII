--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.5

-- Started on 2025-07-17 22:24:24

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 16389)
-- Name: authors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authors (
    id integer NOT NULL,
    name character varying(255),
    version integer
);


ALTER TABLE public.authors OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16388)
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.author_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.author_id_seq OWNER TO postgres;

--
-- TOC entry 4850 (class 0 OID 0)
-- Dependencies: 217
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.author_id_seq OWNED BY public.authors.id;


--
-- TOC entry 219 (class 1259 OID 16395)
-- Name: books; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.books (
    isbn character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    price numeric NOT NULL,
    version integer
);


ALTER TABLE public.books OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16446)
-- Name: books_authors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.books_authors (
    books_isbn character varying(255) NOT NULL,
    authors_id integer NOT NULL
);


ALTER TABLE public.books_authors OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16484)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    id integer NOT NULL,
    name character varying(64) NOT NULL,
    email character varying(64),
    version integer
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16483)
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.customers ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.customers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 227 (class 1259 OID 16583)
-- Name: inventory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.inventory (
    isbn character varying(255) NOT NULL,
    sold integer NOT NULL,
    supplied integer NOT NULL
);


ALTER TABLE public.inventory OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16533)
-- Name: line_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.line_items (
    id integer NOT NULL,
    order_id integer,
    quantity integer NOT NULL,
    isbn character varying(64)
);


ALTER TABLE public.line_items OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16532)
-- Name: line_items1_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.line_items ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.line_items1_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 224 (class 1259 OID 16490)
-- Name: purchase_orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchase_orders (
    id integer NOT NULL,
    customer_id integer,
    total integer,
    status smallint,
    placed_on timestamp without time zone,
    delivered_on timestamp without time zone
);


ALTER TABLE public.purchase_orders OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16489)
-- Name: purchase_orders_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.purchase_orders ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.purchase_orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 4668 (class 2604 OID 16392)
-- Name: authors id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors ALTER COLUMN id SET DEFAULT nextval('public.author_id_seq'::regclass);


--
-- TOC entry 4835 (class 0 OID 16389)
-- Dependencies: 218
-- Data for Name: authors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.authors (id, name, version) FROM stdin;
1	Gabriel García Márquez	1
2	Isabel Allende	1
3	J.K. Rowling	1
4	George R.R. Martin	1
5	Stephen King	1
6	J.R.R. Tolkien	1
7	Haruki Murakami	1
8	Paulo Coelho	1
9	Jane Austen	1
10	Ernest Hemingway	1
\.


--
-- TOC entry 4836 (class 0 OID 16395)
-- Dependencies: 219
-- Data for Name: books; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.books (isbn, title, price, version) FROM stdin;
978-0001	Cien Años de Soledad	20.99	1
978-0002	El Amor en los Tiempos del Cólera	18.5	1
978-0003	Crónica de una Muerte Anunciada	15.75	1
978-0004	El General en su Laberinto	19.2	1
978-0005	La Casa de los Espíritus	17.99	1
978-0006	Paula	16.4	1
978-0007	Eva Luna	18.3	1
978-0008	La Ciudad de las Bestias	14.6	1
978-0009	Harry Potter y la Piedra Filosofal	22.99	1
978-0010	Harry Potter y la Cámara Secreta	23.5	1
978-0011	Harry Potter y el Prisionero de Azkaban	24	1
978-0012	Harry Potter y el Cáliz de Fuego	24.75	1
978-0013	Juego de Tronos	25.99	1
978-0014	Choque de Reyes	26.5	1
978-0015	Tormenta de Espadas	27.1	1
978-0016	Festín de Cuervos	28	1
978-0017	El Resplandor	19.99	1
978-0019	Carrie	17.8	1
978-0020	Misery	18.25	1
978-0021	El Hobbit	21.5	1
978-0022	La Comunidad del Anillo	22.75	1
978-0023	Las Dos Torres	23.2	1
978-0024	El Retorno del Rey	24.1	1
978-0025	Tokio Blues	18.8	1
978-0026	Kafka en la orilla	19.4	1
978-0028	Crónica del pájaro que da cuerda al mundo	21.3	1
978-0029	El Alquimista	16.99	1
978-0030	Brida	15.6	1
978-0031	Veronika decide morir	17.5	1
978-0032	Once minutos	18.1	1
978-0033	Orgullo y Prejuicio	14.5	1
978-0034	Emma	13.9	1
978-0035	Persuasión	14.2	1
978-0036	Sensatez y Sentimientos	14.8	1
978-0037	El viejo y el mar	15.75	1
978-0038	Por quién doblan las campanas	16.4	1
978-0039	Fiesta	15.2	1
\.


--
-- TOC entry 4837 (class 0 OID 16446)
-- Dependencies: 220
-- Data for Name: books_authors; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.books_authors (books_isbn, authors_id) FROM stdin;
978-0001	1
978-0002	1
978-0003	1
978-0004	1
978-0005	2
978-0006	2
978-0007	2
978-0008	2
978-0009	3
978-0010	3
978-0011	3
978-0012	3
978-0013	4
978-0014	4
978-0015	4
978-0016	4
978-0017	5
978-0019	5
978-0020	5
978-0021	6
978-0022	6
978-0023	6
978-0024	6
978-0025	7
978-0026	7
978-0028	7
978-0029	8
978-0030	8
978-0031	8
978-0032	8
978-0033	9
978-0034	9
978-0035	9
978-0036	9
978-0037	10
978-0038	10
978-0039	10
\.


--
-- TOC entry 4839 (class 0 OID 16484)
-- Dependencies: 222
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customers (id, name, email, version) FROM stdin;
1	Ana Pérez	ana.perez@example.com	1
2	Luis Torres	luis.torres@example.com	1
3	Marta Rodríguez	marta.rodriguez@example.com	1
4	Carlos Sánchez	carlos.sanchez@example.com	1
5	Elena Gómez	elena.gomez@example.com	1
6	Pamela	pame200@example.com	2
\.


--
-- TOC entry 4844 (class 0 OID 16583)
-- Dependencies: 227
-- Data for Name: inventory; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.inventory (isbn, sold, supplied) FROM stdin;
978-0001	1	10
978-0002	2	15
978-0005	1	10
978-0006	1	10
978-0007	1	10
978-0009	1	12
978-0010	2	14
978-0013	1	10
978-0014	1	10
978-0017	2	12
978-0021	1	10
978-0022	2	10
978-0023	1	10
978-0025	2	12
978-0026	1	10
978-0029	1	10
978-0030	1	10
978-0031	1	10
978-0033	1	10
978-0034	2	10
978-0038	1	10
978-0039	1	10
\.


--
-- TOC entry 4843 (class 0 OID 16533)
-- Dependencies: 226
-- Data for Name: line_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.line_items (id, order_id, quantity, isbn) FROM stdin;
1	1	1	978-0001
2	1	2	978-0002
3	2	1	978-0005
4	2	1	978-0006
5	2	1	978-0007
6	3	1	978-0009
7	3	2	978-0010
8	4	1	978-0013
9	4	1	978-0014
10	5	2	978-0017
11	6	1	978-0021
12	6	2	978-0022
13	6	1	978-0023
14	7	2	978-0025
15	7	1	978-0026
16	8	1	978-0029
17	8	1	978-0030
18	8	1	978-0031
19	9	1	978-0033
20	9	2	978-0034
21	10	1	978-0037
22	10	1	978-0038
23	10	1	978-0039
24	12	1	978-0002
26	12	3	978-0001
\.


--
-- TOC entry 4841 (class 0 OID 16490)
-- Dependencies: 224
-- Data for Name: purchase_orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.purchase_orders (id, customer_id, total, status, placed_on, delivered_on) FROM stdin;
1	1	50	1	2025-07-05 09:58:18.91016	2025-07-08 09:58:18.91016
2	1	80	2	2025-07-10 09:58:18.91016	2025-07-13 09:58:18.91016
3	2	70	1	2025-07-07 09:58:18.91016	2025-07-10 09:58:18.91016
4	2	100	2	2025-07-12 09:58:18.91016	2025-07-15 09:58:18.91016
5	3	40	1	2025-06-30 09:58:18.91016	2025-07-03 09:58:18.91016
6	3	90	2	2025-07-11 09:58:18.91016	2025-07-14 09:58:18.91016
7	4	60	1	2025-06-25 09:58:18.91016	2025-06-27 09:58:18.91016
8	4	110	2	2025-07-09 09:58:18.91016	2025-07-12 09:58:18.91016
9	5	55	1	2025-07-04 09:58:18.91016	2025-07-06 09:58:18.91016
10	5	85	2	2025-07-13 09:58:18.91016	2025-07-15 09:58:18.91016
12	6	82	1	2025-07-16 22:53:44.550656	\N
\.


--
-- TOC entry 4851 (class 0 OID 0)
-- Dependencies: 217
-- Name: author_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.author_id_seq', 12, true);


--
-- TOC entry 4852 (class 0 OID 0)
-- Dependencies: 221
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customers_id_seq', 6, true);


--
-- TOC entry 4853 (class 0 OID 0)
-- Dependencies: 225
-- Name: line_items1_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.line_items1_id_seq', 26, true);


--
-- TOC entry 4854 (class 0 OID 0)
-- Dependencies: 223
-- Name: purchase_orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.purchase_orders_id_seq', 12, true);


--
-- TOC entry 4670 (class 2606 OID 16394)
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id);


--
-- TOC entry 4674 (class 2606 OID 16685)
-- Name: books_authors book_author_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.books_authors
    ADD CONSTRAINT book_author_pkey PRIMARY KEY (books_isbn, authors_id);


--
-- TOC entry 4672 (class 2606 OID 16401)
-- Name: books books_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_pkey PRIMARY KEY (isbn);


--
-- TOC entry 4676 (class 2606 OID 16488)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);


--
-- TOC entry 4682 (class 2606 OID 16587)
-- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (isbn);


--
-- TOC entry 4680 (class 2606 OID 16537)
-- Name: line_items line_items1_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line_items
    ADD CONSTRAINT line_items1_pkey PRIMARY KEY (id);


--
-- TOC entry 4678 (class 2606 OID 16494)
-- Name: purchase_orders purchase_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_orders
    ADD CONSTRAINT purchase_orders_pkey PRIMARY KEY (id);


--
-- TOC entry 4683 (class 2606 OID 16686)
-- Name: books_authors books_authors_authors_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.books_authors
    ADD CONSTRAINT books_authors_authors_id_fkey FOREIGN KEY (authors_id) REFERENCES public.authors(id) ON DELETE CASCADE;


--
-- TOC entry 4684 (class 2606 OID 16451)
-- Name: books_authors books_authors_books_isbn_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.books_authors
    ADD CONSTRAINT books_authors_books_isbn_fkey FOREIGN KEY (books_isbn) REFERENCES public.books(isbn) ON DELETE CASCADE;


--
-- TOC entry 4688 (class 2606 OID 16588)
-- Name: inventory inventory_isbn_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_isbn_fkey FOREIGN KEY (isbn) REFERENCES public.books(isbn) ON DELETE CASCADE;


--
-- TOC entry 4686 (class 2606 OID 16543)
-- Name: line_items line_items1_isbn_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line_items
    ADD CONSTRAINT line_items1_isbn_fkey FOREIGN KEY (isbn) REFERENCES public.books(isbn) ON DELETE CASCADE;


--
-- TOC entry 4687 (class 2606 OID 16538)
-- Name: line_items line_items1_order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.line_items
    ADD CONSTRAINT line_items1_order_id_fkey FOREIGN KEY (order_id) REFERENCES public.purchase_orders(id) ON DELETE CASCADE;


--
-- TOC entry 4685 (class 2606 OID 16495)
-- Name: purchase_orders purchase_orders_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchase_orders
    ADD CONSTRAINT purchase_orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id) ON DELETE CASCADE;


-- Completed on 2025-07-17 22:24:26

--
-- PostgreSQL database dump complete
--

