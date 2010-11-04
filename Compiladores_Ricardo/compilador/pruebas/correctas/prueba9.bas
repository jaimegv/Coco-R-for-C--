rem prueba9.bas (correcta)

sub resto(a, b)
	static resto
	let resto = a

	while (resto >= b)
		let resto = resto - b
	wend

	let resultado = resto
end sub

print "Ingrese un numero:"
input x

print "Ingrese un numero:"
input y

let resultado = 0
call resto(x, y)
print "El resto de " ; x ; "/" ; y ; " es: " ; resultado

end
