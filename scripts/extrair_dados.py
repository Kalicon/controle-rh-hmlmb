import pdfplumber
import sys
import json
import re

def extrair_dados(caminho_pdf):
    dados = {
        "nome": "",
        "rs": "",
        "pv": 1,
        "cargo": "",
        "regimeJuridico": "Efetivo",
        "dataInicio": None,
        "quantidadeDias": 0,
        "observacoes": "Importado via PDF",
        "erro": None  # Novo: Garante que o campo exista no JSON
    }

    try:
        with pdfplumber.open(caminho_pdf) as pdf:
            if not pdf.pages:
                 print(json.dumps({"erro": "PDF vazio ou ilegível (sem páginas)."}))
                 return

            pagina = pdf.pages[0]
            texto = pagina.extract_text()

            if not texto or len(texto.strip()) == 0:
                 # Tratamento para PDF Escaneado (Imagem)
                 print(json.dumps({"erro": "O PDF parece ser uma imagem escaneada. Só aceitamos PDFs de texto nativo."}))
                 return

            # Regex - RS
            rs_match = re.search(r'RS[:\s]+([\d\.-]+)', texto)
            if rs_match:
                dados["rs"] = rs_match.group(1).replace(".", "").replace("-", "")

            # Regex - Nome
            nome_match = re.search(r'Nome[:\s]+([A-ZÀ-Ÿ\s]+)', texto)
            if nome_match:
                dados["nome"] = nome_match.group(1).strip()

            # Regex - Dias
            dias_match = re.search(r'(\d+)\s*dias', texto)
            if dias_match:
                dados["quantidadeDias"] = int(dias_match.group(1))

            # Regex - Data Início
            data_match = re.search(r'(\d{2}/\d{2}/\d{4})', texto)
            if data_match:
                dia, mes, ano = data_match.group(1).split('/')
                dados["dataInicio"] = f"{ano}-{mes}-{dia}"

        print(json.dumps(dados))

    except Exception as e:
        print(json.dumps({"erro": f"Erro interno do Python: {str(e)}"}))

if __name__ == "__main__":
    if len(sys.argv) > 1:
        extrair_dados(sys.argv[1])
    else:
        print(json.dumps({"erro": "Caminho do PDF não fornecido."}))
