Select all orders that have only digital orders.
Those orders should not be billed for shipping and handling.



select distinct o.orderid, uploadcompleteddate,oipsorderid from orderitems
join orders o on o.orderid = orderitems.orderid
where o.orderid in (
		select orderid from orderitems where productid = 20
		or productid = 19
		or productid = 18
		or productid = 17
)
and o.orderid not in
(
		select distinct o2.orderid 
		from orders o2
		join orderitems oi on oi.orderid = o2.orderid 
		where oi.productid between 1 and 16
)
order by o.orderid;