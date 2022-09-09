package multisig

import (
	"encoding/hex"
	"encoding/json"

	"github.com/MixinNetwork/mixin/crypto"
	mc "github.com/Tougee/multisig-common"
)

func DecodeTransaction(input string) string {
	raw, err := hex.DecodeString(input)
	if err != nil {
		return err.Error()
	}
	ver, err := mc.UnmarshalVersionedTransaction(raw)
	if err != nil {
		return err.Error()
	}
	tm := transactionToMap(ver)
	data, err := json.Marshal(tm)
	if err != nil {
		return err.Error()
	}
	return string(data)
}

func BuildTransaction(data string) string {
	var raw signerInput
	err := json.Unmarshal([]byte(data), &raw)
	if err != nil {
		return err.Error()
	}

	tx := mc.NewTransaction(raw.Asset)
	for _, in := range raw.Inputs {
		tx.AddInput(in.Hash, in.Index)
	}

	for _, out := range raw.Outputs {
		if out.Mask.HasValue() {
			tx.Outputs = append(tx.Outputs, &mc.Output{
				Type:   out.Type,
				Amount: out.Amount,
				Keys:   out.Keys,
				Script: out.Script,
				Mask:   out.Mask,
			})
		}
	}

	extra, err := hex.DecodeString(raw.Extra)
	if err != nil {
		return err.Error()
	}
	tx.Extra = extra

	signed := tx.AsLatestVersion()
	return hex.EncodeToString(signed.Marshal())
}

type signerInput struct {
	Inputs []struct {
		Hash  crypto.Hash  `json:"hash"`
		Index int          `json:"index"`
		Keys  []crypto.Key `json:"keys"`
		Mask  crypto.Key   `json:"mask"`
	} `json:"inputs"`
	Outputs []struct {
		Type     uint8        `json:"type"`
		Mask     crypto.Key   `json:"mask"`
		Keys     []crypto.Key `json:"keys"`
		Amount   mc.Integer   `json:"amount"`
		Script   mc.Script    `json:"script"`
		Accounts []mc.Address `json:"accounts"`
	}
	Asset crypto.Hash `json:"asset"`
	Extra string      `json:"extra"`
}

func (raw signerInput) ReadUTXO(hash crypto.Hash, index int) (*mc.UTXOWithLock, error) {
	return nil, nil
}

func (raw signerInput) CheckDepositInput(deposit *mc.DepositData, tx crypto.Hash) error {
	return nil
}

func (raw signerInput) ReadLastMintDistribution(group string) (*mc.MintDistribution, error) {
	return nil, nil
}

func transactionToMap(tx *mc.VersionedTransaction) map[string]interface{} {
	var inputs []map[string]interface{}
	for _, in := range tx.Inputs {
		if in.Hash.HasValue() {
			inputs = append(inputs, map[string]interface{}{
				"hash":  in.Hash,
				"index": in.Index,
			})
		}
	}

	var outputs []map[string]interface{}
	for _, out := range tx.Outputs {
		output := map[string]interface{}{
			"type":   out.Type,
			"amount": out.Amount,
		}
		if len(out.Keys) > 0 {
			output["keys"] = out.Keys
		}
		if len(out.Script) > 0 {
			output["script"] = out.Script
		}
		if out.Mask.HasValue() {
			output["mask"] = out.Mask
		}
		outputs = append(outputs, output)
	}

	return map[string]interface{}{
		"version":    tx.Version,
		"asset":      tx.Asset,
		"inputs":     inputs,
		"outputs":    outputs,
		"extra":      hex.EncodeToString(tx.Extra),
		"hash":       tx.PayloadHash(),
		"hex":        hex.EncodeToString(tx.PayloadMarshal()),
		"signatures": tx.SignaturesMap,
	}
}
