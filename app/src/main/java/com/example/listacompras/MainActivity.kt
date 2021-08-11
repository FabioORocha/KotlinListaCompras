package com.example.listacompras

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.toast
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //adaptador
        
        val produtosAdapter = ProdutoAdapter(this)
        
        //definindo adaptador a lista

        list_view_produtos.adapter = produtosAdapter

        //botão navegar para Cadastro

        btn_adicionar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)

            startActivity(intent)
        }

        fun deletarProduto(idProduto:Int){
            database.use{
                delete("produtos","id = {id}","id" to idProduto)
            }
        }

        list_view_produtos.setOnItemLongClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->

            val item = produtosAdapter.getItem(i)

            
            
            produtosAdapter.remove(item)

            deletarProduto(item!!.id)

            toast("Item deletado com sucesso")

            true


        }
    }

    override fun onResume() {
        super.onResume()

        database.use{
            select("produtos").exec{
                val parser = rowParser {
                   id: Int, nome: String,
                   quantidade:Int,
                   valor: Double,
                   foto:ByteArray? ->

                   Produto(id, nome, quantidade, valor, foto?.toBitmap())
                }
                val adapter = list_view_produtos.adapter as ProdutoAdapter
                var listaProdutos = parseList(parser)
                adapter.clear()
                adapter.addAll(listaProdutos)

                val soma = listaProdutos.sumByDouble{it.valor * it.quantidade}

                val f = NumberFormat.getCurrencyInstance(Locale("pt","br"))

                txt_total.text= "TOTAL: ${f.format(soma)}"
            }
        }






    }
}