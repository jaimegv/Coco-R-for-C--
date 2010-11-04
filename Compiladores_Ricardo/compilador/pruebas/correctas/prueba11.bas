rem prueba11.bas (correcta)

sub resto(a, b)
	static resto
	let resto = a

	while (resto >= b)
		let resto = resto - b
	wend

	let temporal = resto
end sub

sub binario(numero)
	if (numero > 1) then
		call binario(numero/2)
	
	call resto(numero, 2)
	print temporal
end sub

print "Ingrese un numero:"
input valor
call binario(valor)

end
